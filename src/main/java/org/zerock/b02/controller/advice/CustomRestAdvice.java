package org.zerock.b02.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice { //@valid 과정에서 예외가 생기면 알려주는 컨트롤러

    @ExceptionHandler(BindException.class) //어떤 예외를 핸들할건지 명시
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String,String>> handleBindException(BindException e) {

        log.info("e",e);

        HashMap<String, String> errorMap = new HashMap<>();

        if(e.hasErrors()) {
            BindingResult bindingResult = e.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getCode()); // (필드, 필드에러명) 예) "replyText": "NotEmpty",
            });
        }

        return ResponseEntity.badRequest().body(errorMap); // 상태코드랑 에러객체 hashMap에 담아 전달
    }

}
