package com.zerock.api02.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 어떻게 JWT 를 생성하고, 클라이언트로부터 넘어오는 JWT를 확인하느냐가 포인트!
 * 이 부분은 JWT 관련 라이브러리를 이용해서 처리 -> 가장 많이 사용하는 io.jsonwebtoken 이용
 */

@Component
@Log4j2
public class JWTUtil {

    @Value("${org.zerock.jwt.secret}") //jwt 생성할 때 사용되는 secreat key
    private String key;

    public String generateToken(Map<String,Object> valueMap, int days) { // jwt 문자열을 생성하는 기능

        log.info("generateKey is running..." + key);

        //token은 머리가슴배가 아니라 header, payload, signature 세 부분으로 이뤄짐
        //header (알고리즘,토큰타입)
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("tye","JWT");
        headers.put("alg","HS256");
        
        //payload부분 (data를 claim = (key,value) 형태로 가짐, 민감 정보는 x 최소한 정보만)
        HashMap<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);
        
        //테스트시 시간은 짧게!
        int time = (60*24) * days;

        //Jwts.builder() 정의안했는데 어떻게쓰지?했는데 추가한 라이브러리를 이용!
        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant())) //파라미터로는 days 받는데 구현은 plusMinutes()이용 : 테스트라, 테스트 완료 후 변경
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();


        return jwtStr;
    }

    public Map<String,Object> validateToken(String token) { //token을 검증하는 기능

        //claim : jwt 중 data를 (key,value)형태로 담고 있는 부분 ( jwt는 크게 header(algorithm, token type) & payload( data , claim ) & signature (secret key) 이렇게 3부분으로 구성
        Map<String, Object> claim  = null;

//        Jwt를 검증하는 과정에서, 여러 종류의 예외가 발생할 수 있음 ( 문자열 구성 잘못, 유효 시간 만료, 서명 문제 등) -> 이 검증은 라이브러리의 Jwts.parser() 이용


        claim = Jwts.parser()
                .setSigningKey(key.getBytes()) // set key
                .parseClaimsJws(token) //파싱 및 검증, 실패시 에러 발생
                .getBody(); // token의 세 부분 중 사용자 정보가 담긴 payload(claim) 부분 get

        return claim;

    }


}
