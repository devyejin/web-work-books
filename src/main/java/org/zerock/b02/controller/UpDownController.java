package org.zerock.b02.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b02.dto.upload.UploadFileDTO;
import org.zerock.b02.dto.upload.UploadResultDTO;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {

    //클라이언트 요청에서 첨부파일을 받고나면, 서버에서 어디다 저장할건지 경로가 필요!
    //application.properties에 설정해놓은 경로 읽어오기
    @Value("${org.zerock.upload.path}") //import시 springframework로 시작하는 value (??)
    private String uploadPath;

    @ApiOperation(value = "Upload POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {

        final List<UploadResultDTO> list = new ArrayList<>();

        log.info("uploadFileDTO={}", uploadFileDTO);

        if (uploadFileDTO.getFiles() != null) {

            uploadFileDTO.getFiles().forEach(multipartFile -> {

                //여러 클라이언트가 동일한 이름의 파일을 올릴 수 있으므로 uuid + 파일명으로 구성
                String originalFilename = multipartFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();

                //Paths.get : 매개변수로 주어진 인자들로 저장 경로(파일명 포함) <--- 파일명 포함까지가 경로야
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalFilename);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath); //실제 업로드된 파일을 경로에 저장(transfer)

                    //이미지 파일 -> 썸네일 생성, 이미지외 파일 -> 썸네일 x
                    //책에나온 이미지파일 판단여부로직 넘 별로라 수정
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

                    log.info("originalFilename={}", originalFilename);
                    log.info("fileExtension={}", fileExtension);

                    if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif")) { //이미지 파일이라면 썸네일 파일 생성 (Probes = contentType)

                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalFilename);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200); //(infile경로,outfile쓰일경로,섬네일가로,세로)

                    }
                } catch (IOException e) {
                    log.error(e.getStackTrace());
                }

                //저장하고나면 응답
                list.add(UploadResultDTO.builder().uuid(uuid).fileName(originalFilename).img(image).build());

            });
            return list; //첨부 파일 있는 경우
        }
        return null; //첨부파일을 못읽은 경우 ( 예외 처리 해줘야겠쥬 )
    }

    @ApiOperation(value="view 파일", notes = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {


        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        log.info("resource={}",resource);

        String resourceName = resource.getFilename();
        log.info("resourceName={}",resourceName);
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource. getFile().toPath())); // probeContentType : MIME TYPE을 문자열(Stirng) 반환해주는 메서드(즉, 파일이 어던 종류인지)
            // content-type: image/jpeg  <-- 응답된 결과 (궁금해서)
        } catch (IOException e) {
           return ResponseEntity.internalServerError().build(); //서버에러로 바디에 응답
        }
        return ResponseEntity.ok().headers(headers).body(resource); //200이랑 저장된 파일 경로 응답


    }

    @ApiOperation(value = "remove 파일")
    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete(); //delete() : 성공적으로 삭제되면 true반환

            //썸네일 존재하면 썸네일도 삭제해야함
            if(contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        //응답 용
        resultMap.put("result", removed);

        return resultMap;
    }

}
