package org.zerock.b02.service;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b02.dto.ReplyDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class ReplyServiceImplTest {

    @Autowired
    ReplyService replyService;

    @Test
    void testRegister() {
        //given
        ReplyDTO replyDTO = ReplyDTO.builder().bno(100L).replyText("오늘은 무엇을 해볼까나")
                .replyer("별이가 작성함")
                .regDate(LocalDateTime.now()).build();
        //when
        Long registered = replyService.register(replyDTO);

        //then
        Assertions.assertThat(registered).isNotNull();
        log.info("registed rno={}", registered);
    }
}