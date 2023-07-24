package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    //db에 insert기능은 JpaRepository의 save() 통해 이뤄짐
    //영속 컨텍스트에 엔티티 존재 -> update, 없으면 -> insert
    @Test
    void testInsert() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Board board = Board.builder()
                    .title("테스트 타이틀 입럭 " + i)
                    .content("테스트 컨텐츠 입력 " + i)
                    .writer("테스트 user " + i)
                    .build();

            Board savedBoard = boardRepository.save(board);
            log.info("BNO : " + savedBoard.getBno());
        });
    }


    @Test
    void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        if(result.isPresent()) {
            log.info(result.get());
        }
    }

    @Test
    void testUpdate() {
        //update 기능 또한 JpaRepository의 save() 메서드로 동작
        Optional<Board> findBoardOp = boardRepository.findById(99L);
        Board board = findBoardOp.orElseThrow(); // 있으면 꺼내고 없으면 예외던지고

        log.info("꺼내오고~~~~~");

        board.change("변경되는 타이틀", "변경되는 컨텐츠"); //setter역할

        Board savedBoard = boardRepository.save(board);

        log.info("수정해서 저장하고~~~~~``");

        Assertions.assertThat(savedBoard.getTitle()).isEqualTo("변경되는 타이틀");

    }

    @Test
    void testDelete() {
        boardRepository.deleteById(2L); //영속 컨텍스트에 먼저 가져온 후 delete를 수행하기 때문에 select -> delete 발생

        List<Board> all = boardRepository.findAll();

        Assertions.assertThat(all).size().isEqualTo(98);

        //삭제 전 영속컨텍스트에 저장 후 삭제하기때문에 sel -> del -> 그 담에 확인하느라 가져오니 sel

    }

    /**
     * 페이징 처리는 Pageable 인터페이스 이용  --- able (인터페이스)
     * Jpa Pageable 은 page가 0부터 시작
     */
    @Test
    void testPaging() {
        // 1 page order by bno desc
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);

        //Page 객체는 내부적으로 다양한 페이징 처리를 해줌
        //다음 페이지,이전 페이지 유무, 전체 데이터 갯수 등
        log.info("total count={}", result.getTotalElements());
        log.info("total pages={}", result.getTotalPages());
        log.info("page number={}", result.getNumber()); //현재 페이지
        log.info("page size={}", result.getSize());

        List<Board> todoList = result.getContent();
        todoList.forEach(board -> log.info(board));

    }

    @Test
    void testSearch1() {
        //2page order by bno desc
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }

    @Test
    void testSearchAll() {
        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

    }

    @Test
    void testSearchAll2() {
        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        //total pages
        log.info(result.getTotalPages());

        //page size
        log.info(result.getSize());

        //getNumber
        log.info(result.getNumber());

        //prev next
        log.info(result.hasPrevious() + ": " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));

    }

}