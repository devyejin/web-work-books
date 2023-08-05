package org.zerock.b02.controller;


import com.sun.tools.jconsole.JConsoleContext;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b02.dto.PageRequestDTO;
import org.zerock.b02.dto.PageResponseDTO;
import org.zerock.b02.dto.ReplyDTO;
import org.zerock.b02.service.ReplyService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @ApiOperation(value = "Replies POST", notes = "POST 방식으로 댓글 등록") //swagger api 어노테이션, 문서화
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)  // consumes <- 받아들이는 데이터 타입 명시
    public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO,
                                      BindingResult bindingResult) throws BindException { //@RequestBody <- JSON문자열을 ReplyDTO로 변환해줌
        log.info("replyDTO={}", replyDTO);

        if (bindingResult.hasErrors()) {

            throw new BindException(bindingResult); //BindException 예외가 던져지면 @RestControllerAdvice 에서 처리하게됨
        }

//        Map<String, Long> resultMap = Map.of("rno", 111L);//Map.of() <- 수정불가능한 map 객체를 생성
        HashMap<String, Long> resultMap = new HashMap<>();

        Long rno = replyService.register(replyDTO);

        resultMap.put("rno", rno);

//        return ResponseEntity.ok(resultMap); //상태코드 200을 응답하면서, 안에 담긴 객체도 함께 전달
        return resultMap;

    }

    //특정 게시물 댓글 목록 처리
    @ApiOperation(value ="Replies of Board", notes ="GET방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno,
                                             PageRequestDTO pageRequestDTO) {
        log.info("pageRequestDTO={}", pageRequestDTO.getPage());
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);

        return responseDTO;

    }

    //특정 댓글 조회
    @ApiOperation(value = "Read Reply", notes = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno) {

        //마찬가지로, 클라이언트에서 존재하지않는 rno 요청을 해도 DBMS에서 NoSuchElementException 예외나고, 서버에서 던지다보니 500대 서버에러로 됨
        //ADVICE를통해 클라이언트 에러로 내려주자!
        ReplyDTO readDTO = replyService.read(rno);

        return readDTO;
    }

    @ApiOperation(value = "Delete Reply", notes = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}") //Rest방식은 url은 자원경로만, 메서드기능은 GET,POST,DELETE,PUT 으로
    public Map<String, Long> remove(@PathVariable("rno") Long rno) {

        replyService.remove(rno); //마찬가지로 존재하지 않는 rno 삭제하려고하면 EmptyResultDataAccessException 예외남, Advice에서 예외처리

        HashMap<String , Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }
    @ApiOperation(value="Modify Reply", notes = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value="/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> modify(@PathVariable("rno") Long rno,
                                   @RequestBody ReplyDTO replyDTO) { //여기선 Valid 안해줘도되나?

        //해당 데이터 찾아오기가 아니라, 번호를 일치시키는 방법으로 처리하네?
        replyDTO.setRno(rno);

        replyService.modify(replyDTO);

        HashMap<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);
        return resultMap;
    }
}
