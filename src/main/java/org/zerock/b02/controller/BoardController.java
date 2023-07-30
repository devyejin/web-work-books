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
        redirectAttributes.addFlashAttribute("result",bno); //<- 정상 등록됐는데 굳이 왜?

        return "redirect:/board/list";
    }
}
