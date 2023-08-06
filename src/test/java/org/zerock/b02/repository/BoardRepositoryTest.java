package org.zerock.b02.repository;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    /**
     * JpaRepostiory의 save() 메서드를 사용하면, 영속 컨텍스트에 entity를 저장
     * db에 반영할 때 entity가 미존재시 insert를, entity가 존재하는 경우 update 쿼리를 날림
     */
    @Test
    void testInsert() {
        IntStream.range(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title(i + "번째 타이블")
                    .content(i + "번쨰 컨텐츠")
                    .writer((i % 10) + "번 사용자")
                    .build();

            Board result = boardRepository.save(board);
            log.info("result.getBno()={}", result.getBno());

        });

    }

    @Test
    void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();

        log.info(board);
    }

    @Test
    void testUpate() {
        //given

        Optional<Board> findById = boardRepository.findById(100L);
        log.info("---------------- select 쿼리 나감");
        Board board = findById.orElseThrow();

        //when
        board.change("update title id가 100번인 게시물", "update content  id가 100번인 게시물");
        boardRepository.save(board);
        log.info("-------------------update 쿼리 나감"); // update전에 select쿼리 한번 더 나가는 이유는 dirty checking+동기화

        //then
        Optional<Board> byId = boardRepository.findById(100L);
        log.info(("---------------- select222 쿼리 나감"));
        Board updatedBoard = byId.orElseThrow();
        org.assertj.core.api.Assertions.assertThat(board.getTitle()).isEqualTo(updatedBoard.getTitle());
    }

    @Test
    void testDelete() {
        Long bno = 4L;

        boardRepository.deleteById(bno); //<-- delete전에도 seledt로 영속컨텍스트와 동기화 시킨 후 delete작업이 발생함
    }

    /**
     * Pageable 인터페이스를 이용헤서 페이징 처리를 함
     * PageRequest.of를 사용하는데 PageRequest는 Pageable 상속받아 구현한 구현체
     */
    @Test
    void testPaging() {
        // 1page order by bno desc
        Pageable pageable = PageRequest.of(3, 15, Sort.by("bno").descending()); //JPA 페이징은 0부터 시작

        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count = {}", result.getTotalElements());
        log.info("tota pages = {}", result.getTotalPages()); //총 페이지수
        log.info("page number = {}", result.getNumber()); //현제 페이지 번호
        log.info("page size = {}", result.getSize()); //the size of the Slice (페이지 구성 size, 즉 한 페이지당 몇 개의 데이터 출력인지)

        List<Board> todoList = result.getContent();
        todoList.forEach(board -> log.info(board));

    }

    @Test
    void testSearch1() {
        //2 page order by bno desc
        Pageable pageable = PageRequest.of(2, 10, Sort.by("bno").descending());

        Page<Board> boards = boardRepository.search1(pageable);
    }

    @Test
    void testSearchAll() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        String[] types = {"t","c","w"};
        String keyword = "1";

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        //total pge(총 페이지 수)
        log.info(result.getTotalPages());

        //page size(한 페이지 당 구성되는 게시물 수)
        log.info(result.getSize());

        //pageNumber(현재 페이지 번호)
        log.info(result.getNumber());

        //prev next 유무 ( = 기호가 아니라 : 기호임)
        log.info("result.hasPrevious : {}, result.hasNext : {}", result.hasPrevious(),result.hasNext());

        result.getContent().forEach(board -> log.info(board));

    }

    @Test
    @DisplayName("검색조건 넣고 검색해서 목록 반환할건데, reply 달린 갯수도 함께 출력")
    void testSearchWithReplyCount() {
        //given
        String[] types = {"t","w","c"}; // title, writer, content 다 포함해서 검색 가정
        String keyword = "1";
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        //when
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        //then
        Assertions.assertNotNull(result); //뭐라도 걸리는 데이터 있겠지..

        log.info("totalPage={}",result.getTotalPages());
        log.info("size={}", result.getSize()); //page size
        log.info("page number={}", result.getNumber()); //page number
        log.info("result.hasPrevious()={}, result.hasNext()={}", result.hasPrevious(),result.hasNext());


        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    void testInsertWithImages(){

        //given
        Board board = Board.builder()
                .title("attach image test")
                .content("첨부파일 이미지 테스트")
                .writer("tester")
                .build();
        //when
        for(int i=0; i<3; i++) {
            board.addImage(UUID.randomUUID().toString(), "file"+i+".jpeg");
        }
        Board savedBoard = boardRepository.save(board);

        //then
        Assertions.assertThat(savedBoard.getImageSet().size()).isEqualTo(3);
        savedBoard.getImageSet().forEach(image -> log.info(image));

    }



}