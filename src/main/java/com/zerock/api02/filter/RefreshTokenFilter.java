package com.zerock.api02.filter;

import com.google.gson.Gson;
import com.zerock.api02.security.exception.RefreshTokenException;
import com.zerock.api02.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * 클라이언트의 accessToken 만료시 refreshToken을 통해 accessToken 갱신해달라고 요청
 * OncePerRequestFilter : 하나의 요청에 대해 한번씩 동작하는 필터
 * <p>
 * '/refreshToken' url을 통해 사용자가 accessToken, refreshToken을 전송 -> API Server에서 새로운 accessToken, refreshToken을 발급
 * <p>
 * 서버에서의 동작
 * - access token 존재 여부
 * - refresh token 만료 여부 확인
 * - refresh token 만료 기간이 지났다면, 다시 인증을 해서 둘 다 새로운 토큰을 발급해서 전달 (즉, access Token만 만드는경우, access & refresh 둘 다 만드는 경우 )
 */

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter { //마찬가지로, Filter를 Config에 등록해줘야겠쥬?!

    private final String refreshPath; //발급해달라는 경로 받음
    private final JWTUtil jwtUtil; //토큰 발급시 필요

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.equals(refreshPath)) {
            log.info("skip refresh token filter"); //oh
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Refresh Token Filter...run..........!");

        //1. JSON데이터에서 access, refresh 토큰 추출
        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken={}",accessToken);
        log.info("refreshToken={}",refreshToken);

        //2. access token 검증, 토큰이 없거나 잘못된 경우 에러 메시지 전송

        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.snedResponseError(response);
            return; // 클라이언트한테 error 메시지 보내주고, 더 이상 실행할 필요X
        }

        Map<String,Object> refreshClaims = null;

        //3. refresh token 존재여부, 만료일 검사 -> 새로 생성해야할 수도 있으니 mid get
        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info("refreshClaims={}",refreshClaims);

        }catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.snedResponseError(response);
            return; //클라이언트한테 응답내려줬으니 더 이상 로직 진행x
        }

        //4. 검증이 끝났으니 new 토큰 발행, access -> 무조건 새로 발행, refresh -> 만료일이 얼마 안남은 경우 새로 발행

        //유효기간이 얼마 안남은 경우
        Integer exp = (Integer) refreshClaims.get("exp");

        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
        Date current = new Date(System.currentTimeMillis());

        log.info("expTime={}",expTime);
        log.info("current={}",current);


        //만료 시간과 현재 시간 간격 계산
        //만료시간 - 현재시간 < 3일미민 => refresh Token도 새로 생성
        long gapTime = (expTime.getTime() - current.getTime());
        log.info("gapTime={}",gapTime);

        String mid = (String)refreshClaims.get("mid");

        //이 상태까지 왔다면 accesstoken은 새로 생성 <-- refresh가 만료가가깝다는건 access는 당연히 만료됐을테니까
        String accessTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 1);

        String refreshTokenValue = tokens.get("refreshToken");

        //refresh Token이 3일도 안 남은 경우, 새로운 Refresh Token 생성
        if(gapTime < (1000*60*60*24*3)) {
            log.info("new Refresh Token required.........");
            refreshTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 30);//payload부분에 들어가는거니까 클라이언트 정보
        }

        log.info("Refresh Token generate result.........");
        log.info("accessTokenValue={}", accessTokenValue);
        log.info("refreshTokenValue={}",refreshTokenValue);

        //이제 생성된 토큰 클라이언트에 전달
        snedTokens(accessTokenValue,refreshTokenValue,response);


    }

    //생성된 토큰 전송 메서드
    private void snedTokens(String accessTokenValue,String refreshTokenValue, HttpServletResponse response) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));

        try {
           response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) {

        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);
            return values; //예외 없을 시(정상)

        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        }catch (MalformedJwtException malformedJwtException) { //토큰값이 올바르지 못한 경우 예외
            log.error("MalformedJwtException...........");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }catch (Exception exception) {
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }

        return null;

    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {

        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has expired!!!");
        } catch (Exception exception) { //토큰이 없거나 잘못된 경우
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }

    }

    private Map<String,String> parseRequestJSON(HttpServletRequest request) {

        // 클라이언트가 보낸 JSON데이터 분석해서 mid,mpw 값을 Map으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class); // 읽은 json을 Map으로 만들어라

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }



}
