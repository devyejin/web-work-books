package com.zerock.api02.util;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class JWTUtilTest {

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("jwt 토큰 문자열 생성 테스트")
    void testGenerate() {

        //given
        Map<String, Object> claimMap = Map.of("mid", "ABCDE"); //ㅇㅎ token을 만들 때 data를 이용하는구나 (그래서 민감 데이터는 들어오면 안됨 decode 가능하니까)

        //when
        String jwtStr = jwtUtil.generateToken(claimMap, 1);// token을 만들 때 data(claim - (key,value로 이뤄짐) ) 을 이용!

        //then
        log.info("jwtStr={}",jwtStr);
        Assertions.assertNotNull(jwtStr);

    }

   // @Test
    @DisplayName("expired token test")
    void testValidate() {

        //given
        String jwtStr = "eyJ0eWUiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTE3NzUxMzksIm1pZCI6IkFCQ0RFIiwiaWF0IjoxNjkxNzc1MDc5fQ.oWT-M3WWNso7H_vuc_UqJ6ZnYWmFE_42btMH0AlLae0";


        //when (검증시)
        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

    }

    @Test
    @DisplayName("generate token and validation")
    void testAll() {
        //given
        String jwtStr = jwtUtil.generateToken(Map.of("mid", "AAAA", "email", "AAA.@naver.com"), 1);
        log.info("jwtStr={}",jwtStr);

        //when
        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);
        log.info("claim={}",claim);

        //then
        log.info("claim.id={}",claim.get("mid"));
        log.info("claim.email={}",claim.get("email"));
        Assertions.assertNotNull(claim);
    }
}