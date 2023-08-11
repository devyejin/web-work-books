package com.zerock.api02.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 어떻게 JWT 를 생성하고, 클라이언트로부터 넘어오는 JWT를 확인하느냐가 포인트!
 * 이 부분은 JWT 관련 라이브러리를 이용해서 처리 -> 가장 많이 사용하는 io.jsonwebtoken 이용
 */

@Component
@Log4j2
public class JWTUtil {

    @Value("${org.zerock.jwt.secret}")
    private String key;
}
