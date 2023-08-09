package org.zerock.b02.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b02.dto.*;
import org.zerock.b02.service.BoardService;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    
    //첨부파일 삭제를 위해, 경로 주입 (application.properties)
    @Value("${org.zerock.upload.path}") // 스프링프레임웤 지원 어노테이션
    private String uploadPath;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {

//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);

        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        log.info("responseDTO={}",responseDTO);

        model.addAttribute("responseDTO",responseDTO);
        //view로 pageRequestDTO, responserDTO 둘 다 전달
    }

    @GetMapping("/register")
    public void registerGet() {

    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        log.info("board register call! ");

        //에러처리 먼저, 에러 -> 등록페이지로
        //난 에러처리 다른 방식으로 하고 싶음

        if(bindingResult.hasErrors()) {
            log.error("BoardDTO has errors........");

            //등록하던 페이지로 어느 부분에 에러가 있는지 알려주기 (RedirectAttributes : 리다이렉트시 데이터 전달하는 객체(model) )
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/register";

        }

        log.info("boardDTO={}",boardDTO);

        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result",bno); //<- 정상 등록됐는데 굳이 왜? => 모달 창에 활용
        //addFlashAttribute에 담긴 데이터는 쿼리 스트링으로 처리되지 않아 url에는 보이지않음, 일회성 데이터 전송

        return "redirect:/board/list";
    }

    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("boardDTO={}", boardDTO);

        model.addAttribute("dto",boardDTO);
    }

    //수정 후에는 검색조건이 변경되기때문에, list페이지로 보낼 때 평범한 첫 페이지로
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                       @Valid BoardDTO boardDTO,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {

        log.info("boardDTO={}", boardDTO);

        if(bindingResult.hasErrors()) {
            log.info("has errors.... check please"); //에러정보를 담아서 다시 modify페이지로

            String link = pageRequestDTO.getLink(); // link에는 bno정보는 없는데...?

            //addFlashAttribute의 경우, 리다이렉트요청시 까지만 생존해서 url 쿼리파라미터로 안담김
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno", boardDTO.getBno());

            //link, bno 정보가 GET요청으로 가니까 쿼리파라미터에 담기는거같음!
            //예)http://localhost:8080/board/modify?page=1&size=10&bno=206
            return "redirect:/board/modify?"+link;
        }

        boardService.modify(boardDTO);

        redirectAttributes.addFlashAttribute("result","modified");
        redirectAttributes.addAttribute("bno", boardDTO.getBno());

        //예) http://localhost:8080/board/read?bno=206
        return "redirect:/board/read";

    }

//    @PostMapping("/remove")
//    public String remove(Long bno, RedirectAttributes redirectAttributes) {
//
//        log.info("call remove post .... " + bno);
//
//        boardService.remove(bno);
//
//        redirectAttributes.addFlashAttribute("result", "removed");
//
//        return "redirect:/board/list";
//    }
    
    //첨부파일 제거까지 담당, 이제 bno가 아닌 BoardDTO로 받아야함 ( 첨부파일에 대한 정보도 받아야하니까)
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {

        Long bno = boardDTO.getBno();
        log.info( bno);
        
        boardService.remove(bno);
        
        //게시물이 DB에서 삭제되었다면, 첨부파일도 삭제
        log.info("boardDTO.getFileNames={}",boardDTO.getFileNames());

        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0) { //파일이 있다면 삭제해!
            removeFiles(fileNames);
        }
        
        redirectAttributes.addFlashAttribute("result", "removed"); //일회성으로 보내주면되니까 Flash 이용
        
        return "redirect:/board/list"; //PRG 또 삭제요청 들어가면 에러나니까 G으로 보내버리기
    }


    public void removeFiles(List<String> files) {
        for(String fileName : files) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceFilename = resource.getFilename();

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                
                resource.getFile().delete(); //첨부파일 삭제
                
                //썸네일이 존재하는 경우, 썸네일도 삭제 (그래서 contentType확인 이미지면 있으니까)
                if(contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    
                    thumbnailFile.delete(); //썸네일도 삭제 끝!
                }
                
            } catch (IOException e) {
               log.error(e);
            }
        }
    }
}
