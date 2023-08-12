package com.zerock.api02;

import com.zerock.api02.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

/**
 * APILoginFilter 에서 인증 처리 완료 후, 우리가 원하는건 클라이언트한테 토큰을 발행해주는 것
 * 그런데, 기존 방식처럼 인증 성공하면 index로 리다이렉트 시켜주는 '/'가 발생함. 우린 restapi라 필요가없는데!
 * 그래서, 후처리 handler  'APILoginSuccessHandler' 를 커스터마이징해서 토큰발행형식으로 해주자!
 *
 * APILoginFilter에서 인증 및 Token을 발생, Token 발급시 AuthenticationManager에게 Token 전달
 * 성공시 APILoginSuccessHandler이 동작하는데(cofing에 등록해놔서), 이제 Handler가 클라이언트한테 Access, Refresh Token을 응답!
 * 이 때 JWTUTil이용
 */


@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler { //글구 SecurityConfig에 핸들러 등록도해줘야함

    private final JWTUtil jwtUtil; //여기서 이렇게 한다고만 쓸 수 있는게 아님! SecurityConfig에서 주입해줘야함! 주의!
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success Handler..........");

        //APILoginFilter에서 token발행 성공하고 authenticationManager를 통해 token이 넘어오면 그걸 클라이언트에게 보내줘야함
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info("authentication={}",authentication);
        log.info("authentication.getName={}",authentication.getName()); //username


        Map<String, Object> claim = Map.of("mid", authentication.getName());

        String accessToken = jwtUtil.generateToken(claim, 1);//token 유효기간 1일
        String refreshToken = jwtUtil.generateToken(claim, 30);// refresh token 유효기간 30일

        Gson gson = new Gson();

        Map<String, String> keyMap = Map.of("accessToken", accessToken, "refreshToken", refreshToken);

        String jsonStr = gson.toJson(keyMap);
        log.info("jsonStr={}",jsonStr);

        response.getWriter().println(jsonStr); // access,refresh token 전달


    }
}
