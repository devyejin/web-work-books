package org.zerock.b02.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b02.dto.BoardDTO;
import org.zerock.b02.dto.PageRequestDTO;
import org.zerock.b02.dto.PageResponseDTO;
import org.zerock.b02.service.BoardService;

import javax.validation.Valid;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

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

    @PostMapping("/remove")
    public String remove(Long bno, RedirectAttributes redirectAttributes) {

        log.info("call remove post .... " + bno);

        boardService.remove(bno);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";
    }
}
