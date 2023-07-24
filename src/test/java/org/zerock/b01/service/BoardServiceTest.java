package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.BoardDTO;

import static org.junit.jupiter.api.Assertions.*;


@Log4j2
@SpringBootTest
class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    void testRegister() {
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder().title("데이터 추가 테스트")
                .content("컨텐츠 추가")
                .writer("user00")
                .build();

        Long bno = boardService.register(boardDTO);
        log.info("bno={}",bno);
    }

    @Test
    void testModify() {

        BoardDTO boardDTO = BoardDTO.builder().bno(101L).title("수정수정수정").content("수정수정수정").build();

        boardService.modify(boardDTO);

    }

}