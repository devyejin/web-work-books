package org.zerock.b02.repository;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b02.domain.Board;
import org.zerock.b02.domain.Reply;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    void testInsert() {
        Long bno = 300L;

        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("댓글 추가요추가요추가요추가요.....")
                .replyer("replyer333333")
                .build();

        replyRepository.save(reply);

    }

    @Test
    @Transactional
    void testIistOfBoard() {
        //게시글 번호를 통한 댓글 페이징 처리
        //given
        Long bno = 205L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());

        //when
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        //list처리 해야지
        result.getContent().forEach(reply -> log.info("reply={}", reply));
        List<Reply> replyList = result.getContent();

        //then
        Assertions.assertThat(replyList).size().isEqualTo(3);

        /**
         * 위 코드는 지연로딩(reply테이블만 조회)라 ㅇㅋ
         * 만약 Entity에 지연로딩 or reply객체 출력(@Tostring에서 board제외)를 안했으면 board테이블 조회도 하려고하는데,
         * 네트워크가 닫혀버려서 exception 발생함 -> 트랜잭션 처리가 필요
         */
    }


}