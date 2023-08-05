package org.zerock.b02.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b02.dto.upload.UploadFileDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@Log4j2
public class UpDownController {

    //클라이언트 요청에서 첨부파일을 받고나면, 서버에서 어디다 저장할건지 경로가 필요!
    //application.properties에 설정해놓은 경로 읽어오기
    @Value("${org.zerock.upload.path}") //import시 springframework로 시작하는 value (??)
    private String uploadPath;

    @ApiOperation(value="Upload POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(UploadFileDTO uploadFileDTO) {
        log.info("uploadFileDTO={}", uploadFileDTO);

        if(uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach( multipartFile -> {

                //여러 클라이언트가 동일한 이름의 파일을 올릴 수 있으므로 uuid + 파일명으로 구성
                String originalFilename = multipartFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();

                //Paths.get : 매개변수로 주어진 인자들로 저장 경로(파일명 포함) <--- 파일명 포함까지가 경로야
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalFilename);

                try {
                    multipartFile.transferTo(savePath); //실제 업로드된 파일을 경로에 저장(transfer)

                    //이미지 파일 -> 썸네일 생성, 이미지외 파일 -> 썸네일 x
                    //책에나온 이미지파일 판단여부로직 넘 별로라 수정
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")+1).toLowerCase();

                    if(fileExtension.equals("jpq") || fileExtension.equals("jpeg")||fileExtension.equals("png")||fileExtension.equals("gif")) { //이미지 파일이라면 썸네일 파일 생성 (Probes = contentType)
                        log.info("썸네일 파일 생성 호출됨");
                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalFilename);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200); //(infile경로,outfile쓰일경로,섬네일가로,세로)

                    }
                } catch (IOException e) {
                    log.error(e.getStackTrace());
                }

            });
        }
        return null;
    }
}
