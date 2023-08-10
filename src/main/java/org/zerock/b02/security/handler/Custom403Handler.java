package org.zerock.b02.security.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler { //이 ExceptionHandler를 SecurityConfig에 등록해줘야함!


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info("-------ACCESS DENIED---------------");

        response.setStatus(HttpStatus.FORBIDDEN.value());

        //Ajax 요청인 경우 JSON으로 넘어옴, JSON인지 확인 => JSON인 경우, JSON데이터를 만들어서 전송
        String contentType = request.getHeader("Content-Type");

        boolean jsonRequest = contentType.startsWith("application/json");
        log.info("isJson={}",jsonRequest);

        //일반 from요청인 경우 -> 로그인 페이지로 이동할 때 ACCESS_DENIED 를 파라미터로 전달
        if(!jsonRequest) {
            response.sendRedirect("/member/login?error=ACCESS_DENIED");
        }
    }
}
