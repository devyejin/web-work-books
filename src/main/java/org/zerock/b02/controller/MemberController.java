package org.zerock.b02.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b02.dto.MemberJoinDTO;
import org.zerock.b02.service.MemberService;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 스프링 시큐리티를 통해 로그인처리를 하는경우, SecurityConfig 에서 로그인 처리를할 때 POST요청까지 받고 내부에서 처리함
     * 컨트롤러에서 POST요청을 처리 안한다!, 별도로 구현하고 싶다면 별도로 지정 가능, 로그이웃 기능도 시큐리티가 처리해줌
     * - 로그아웃의 경우 시큐리티는 HttpSession을 이용해서 처리, 직접 쿠키(JSESSIONID 쿠키) 삭제하면 자동 로그아웃 처리됨
     */
    @GetMapping("/login")
    public void loginGET(String error, String logout) { //로그인 과정에서 문자 발생, 로그아웃 처리시 사용
        log.info("login get request..............");
        log.info("logout={}",logout);

        //로그아웃 여부를 확인하고싶다면 logout arguments 이용
        //로그아웃하고프면  다이렉트로 /logout요청
        if(logout != null) {
            log.info("user logout state.........");
        }
    }

    @GetMapping("/join")
    public void joinGET() {

        log.info("join get call.");
    }

    @PostMapping("/join")
    public String joinPost(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes) {

        log.info("join post call.");
        log.info("memberJoinDTO={}", memberJoinDTO);

        try {
            memberService.join(memberJoinDTO);
        } catch (MemberService.MidExistException e) { //이미 존재하는 id라고 예외나면
            // 회원가입창으로 다시 보내기
            redirectAttributes.addFlashAttribute("error","mid");
            return "redirect:/member/join";
        }

        //예외없이 정상 가입(DB저장)되면
        redirectAttributes.addFlashAttribute("result","success");

        return "redirect:/member/login"; //회원 가입 후 로그인 창으로

    }


}
