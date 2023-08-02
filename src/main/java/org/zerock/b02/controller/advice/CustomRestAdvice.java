package org.zerock.b02.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
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

    /**
     * valid 중 클라이언트가 존재하지 않는 bno 요청이, SQLException 발생함 (dbms에서 DataIntegrityViolationException.class 넘어옴)
     * 근데, 이게 타고타고 전해지다 서버에서 에러내니까 클라이언트한테는 서버에러 500대가 넘어감
     * 그러면 클라이언트는 내가 입력값을 잘못넣었다는걸 인지못하고 서버가 이상하구나~하게되니까 Excpetion을 다르게 처리해줘야함
     *
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) //417 요청 헤더의 요구값을 만족시키지 못해서 서버에서 요청 거부
    public ResponseEntity<Map<String,String>> handlerFKException(Exception e) {
        log.error(e);

        HashMap<String, String> errorMap = new HashMap<>();

        errorMap.put("time", ""+System.currentTimeMillis());
        errorMap.put("msg", "constraint fails");
        return ResponseEntity.badRequest().body(errorMap);
    }

}
