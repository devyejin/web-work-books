package org.zerock.b02.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.b02.security.dto.MemberSecurityDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 스프링 시큐리티는 인증(로그인,Authentication) 성공과 실패를 커스터마이징하도록 AuthenticationSuccessHandler , AuthenticationFaileHandler 인터페이스 제공
 *
 * 소셜 로그인 성공 후 현재 사용자의 패스워드에 따라 사용자 정보를 수정하거나 특정 페이지로 이동하는 방법을 처리해야함. (기본 셋팅은 우선 1111로 해놓고)
 *
 */
@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSucessHandler implements AuthenticationSuccessHandler { //<-- 핸들러도 마찬가지로 Config에 등록해줘야함

    private final PasswordEncoder passwordEncoder;

    //기본셋팅 비번에서 사용자가 변경해야함
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("------------------------------");
        log.info("CustomSocialLoginSucessHandler onAuthenticationSuccess method call!! ");
        log.info("principal={}",authentication.getPrincipal());

        //인증정보에서 사용자 정보 get
        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO)authentication.getPrincipal();

        String encodedPw = memberSecurityDTO.getMpw();

        //소셜 로그인 + 회원 비번이 1111인 경우
        if(memberSecurityDTO.isSocial()
                && (memberSecurityDTO.getMpw()).equals("1111")
                || passwordEncoder.matches("1111", memberSecurityDTO.getMpw())) { //자동 가입된 회원도 encorder를 통해 1111을 인코딩한 상태라 mathces() 이용해서 검사

            log.info("Should Change Password");

            log.info("Redirect to Member Modify Page");
            response.sendRedirect("/member/modify");
            return;
        } else {
            response.sendRedirect("/board/list");
        }
    }
}
