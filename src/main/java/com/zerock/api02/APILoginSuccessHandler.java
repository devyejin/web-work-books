package com.zerock.api02;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * APILoginFilter 에서 인증 처리 완료 후, 우리가 원하는건 클라이언트한테 토큰을 발행해주는 것
 * 그런데, 기존 방식처럼 인증 성공하면 index로 리다이렉트 시켜주는 '/'가 발생함. 우린 restapi라 필요가없는데!
 * 그래서, 후처리 handler  'APILoginSuccessHandler' 를 커스터마이징해서 토큰발행형식으로 해주자!
 */


@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler { //글구 SecurityConfig에 핸들러 등록도해줘야함

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success Handler..........");
    }
}
