package org.zerock.b02.controller;


import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b02.dto.ReplyDTO;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
public class ReplyController {

    @ApiOperation(value="Replies POST", notes = "POST 방식으로 댓글 등록") //swagger api 어노테이션, 문서화
    @PostMapping(value="/", consumes = MediaType.APPLICATION_JSON_VALUE)  // consumes <- 받아들이는 데이터 타입 명시
    public ResponseEntity<Map<String,Long>> register(@Valid @RequestBody ReplyDTO replyDTO,
                                                     BindingResult bindingResult) throws BindException { //@RequestBody <- JSON문자열을 ReplyDTO로 변환해줌
        log.info("replyDTO={}",replyDTO);

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult); //BindException 예외가 던져지면 @RestControllerAdvice 에서 처리하게됨
        }

        Map<String, Long> resultMap = Map.of("rno", 111L);//Map.of() <- 수정불가능한 map 객체를 생성

        return ResponseEntity.ok(resultMap); //상태코드 200을 응답하면서, 안에 담긴 객체도 함께 전달

    }
}
