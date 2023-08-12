package com.zerock.api02.security.exception;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class AccessTokenException extends RuntimeException{

    TOKEN_ERROR token_error;

    public AccessTokenException(TOKEN_ERROR error) {
        super(error.name());
        this.token_error = error;
    }

    //error 클라이언트한테 response
    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(token_error.getStatus()); //응답상태코드
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String responseStr = gson.toJson(Map.of("msg", token_error.getMsg(), "time", new Date()));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //error 타입을 enum으로 정의
    public enum TOKEN_ERROR {
        UNACCEPT(401,"Token is null or too short"),
        BADTYPE(401, "Token type Bearer"),
        MALFORM(403, "Malformed Token"),
        BADSIGN(403, "BadSignatured Token"),
        EXPIRED(403, "Expired Token");

        private int status;
        private String msg;

        TOKEN_ERROR(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

    }
}
