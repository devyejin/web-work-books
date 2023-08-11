package com.zerock.api02.filter;


import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * JWT 토큰 발급을 컨트롤러가 아닌 필터에서 처리
 * 즉, 인증 단계를 필터에서 처리하고 인증 성공시 토큰을 전송
 * <p>
 * APILoginFilter 필터는 기존 필터들과 달리 '로그인 처리를 담당' -> 로그인을 처리하는 경로에 대한 설정, 실제 인증 처리를 담당하는 Authentication Manager 객체 설정 필요
 * => 설정은 CustomSecurityConfig 이용
 */


@Log4j2
public class APILoginFilter extends AbstractAuthenticationProcessingFilter {

    //생성자
    public APILoginFilter(String defaultFilterProcessUrl) {
        super(defaultFilterProcessUrl);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("APILogin Filter...................");
        // "/generateToken" 요청이 들어오면   APILoginFilter 필터에서 사용자의 mid, mpw를 이용해서 JWT문자열(토큰)을 생성하는 기능을 수행해야함
        //즉, 사용자가 전달하는 mid, mpw값을 알아내야하는데, API서버는 POST방식으로 JSON문자열을 이용! -> 라이브러리 GSON이용

        if (request.getMethod().equalsIgnoreCase("GET")) {
            log.info("get method is not support ");
            return null;
        }

        //이제 post요청만 처리될 테니까, GSON라이브러리를 통해서 request에서 담긴 id, pwd를 JSON문자열로 받는 처리를 하자
        Map<String, String> jsonData = parseRequest(request);
        log.info("jsonData={}", jsonData); // 이 과정을 거쳐 회원의 id와 pwd 를 알 수 있음


        // mid, mpw를 알았으니 이제 이걸 담은 토큰 인증정보를 만들고-> UsernamePsswordAuthenticationFilter에서 작업하도록 하기
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
                jsonData.get("mid"),
                jsonData.get("mpw"));

        return getAuthenticationManager().authenticate(authenticationToken); //매니저한테 넘기면 다음 필터로 전달?
        

    }

    private Map<String, String> parseRequest(HttpServletRequest request) {

        //JSON 데이터를 분석해서 mid, mpw 전달 값을 Map으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class); //읽어온 데이터를 Map으로

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
