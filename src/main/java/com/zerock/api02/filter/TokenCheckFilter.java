package com.zerock.api02.filter;

import com.zerock.api02.security.exception.AccessTokenException;
import com.zerock.api02.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 하나의 요청에 대해 한번씩 동작하는 필터
 * JWT 토큰을 검사하는 역할 ( 유효기간 등 ) => JWTUtil 의 validateToken() 기능 이용
 *
 * 밑에 로직에 의해 /api/** 로 들어오는 요청들은 TokenCheckFilter 필터를 거침!
 * 예외처리를 해줘야하는데
 * 1) access token 없는 경우 -> 토큰이 없다는 메시지 전달
 * 2) access token 잘못된 경우 -> 잘못된 토큰 이라는 메시지 전달
 * 3) access token 만료된 경우 -> 토큰 갱신 하라는 메시지 전달
 *
 * 위 같은 작업들을 하려면 먼저, accesstoken을 얻어야함. token은 HTTP Header 중 Authorization(인가) 헤더 를 통해 전달됨
 * Authorization header 는 'type+인증값'으로 구성, type에는 Basic, Bearer, Digest, HOBA, Mutual등이 있음 OAuth, JWT는 Bearer타입 사용
 */

@RequiredArgsConstructor
@Log4j2
public class TokenCheckFilter extends OncePerRequestFilter { //마찬가지로 TokenCheckFilter 도 Config에 등록

    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) { // 즉, /api/** 로 들어오는 요청들은 TokenCheckFilter 필터를 거침!, 아닌 요청은 거르기
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Token Check Filter............");
        log.info("jwtUtil={}", jwtUtil);

        try {
            validateAccessToken(request);
            filterChain.doFilter(request,response);
        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response); //클라이언트한테 에러 response
        }

        filterChain.doFilter(request, response);
    }

    //access token 검증, 검증하다 에러나면 에러처리
    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException{

        String headerStr = request.getHeader("Authorization");

        if (headerStr == null || headerStr.length() <= 0) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        //Bearer 생략
        String tokenType = headerStr.substring(0, 1);
        String tokenStr = headerStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false) { // jwt가 Bearer type이라 아닌것들 필터링
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);

            return values;
        } catch(MalformedJwtException malformedJwtException) { //MalformedJwtException jwt 라이브러리 에서 지원하는 기능, 비정상적인jwt
            log.error("MalformedJwtException occur!");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch(SignatureException signatureException) {
            log.error("SignatureException occur!");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException occur!");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
        
    }

}