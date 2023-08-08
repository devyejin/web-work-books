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
import org.springframework.test.annotation.Commit;
import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.BoardListAllDTO;
import org.zerock.b02.dto.BoardListReplyCountDTO;

import javax.transaction.Transactional;
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

    @Autowired
    private ReplyRepository replyRepository;

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

        String[] types = {"t", "c", "w"};
        String keyword = "1";

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        //total pge(총 페이지 수)
        log.info(result.getTotalPages());

        //page size(한 페이지 당 구성되는 게시물 수)
        log.info(result.getSize());

        //pageNumber(현재 페이지 번호)
        log.info(result.getNumber());

        //prev next 유무 ( = 기호가 아니라 : 기호임)
        log.info("result.hasPrevious : {}, result.hasNext : {}", result.hasPrevious(), result.hasNext());

        result.getContent().forEach(board -> log.info(board));

    }

    @Test
    @DisplayName("검색조건 넣고 검색해서 목록 반환할건데, reply 달린 갯수도 함께 출력")
    void testSearchWithReplyCount() {
        //given
        String[] types = {"t", "w", "c"}; // title, writer, content 다 포함해서 검색 가정
        String keyword = "1";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        //when
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        //then
//        Assertions.assertNotNull(result); //뭐라도 걸리는 데이터 있겠지..

        log.info("totalPage={}", result.getTotalPages());
        log.info("size={}", result.getSize()); //page size
        log.info("page number={}", result.getNumber()); //page number
        log.info("result.hasPrevious()={}, result.hasNext()={}", result.hasPrevious(), result.hasNext());


        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    @DisplayName("하나의 게시물에 첨부파일 3개 추가 테스트")
    void testInsertWithImages() {

        //given
        Board board = Board.builder()
                .title("attach image test")
                .content("첨부파일 이미지 테스트")
                .writer("tester")
                .build();

        //when
        for (int i = 0; i < 3; i++) {
            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpeg");
        }
        Board savedBoard = boardRepository.save(board); //이때 쿼리문을 확인하면 board insert문 1번, board_image insert문 3번 나감

        //then
        Assertions.assertThat(savedBoard.getImageSet().size()).isEqualTo(3);
        savedBoard.getImageSet().forEach(image -> log.info(image));

    }

    @Transactional
    @Test
    void testReadWithImages() {

        //@OneToMany 의 경우 lazy로딩이 default.
        //즉, board에서 board_image를 관리하지만, 사용하지 않을 때는 board_image는 조회하지 않음, 필요시 조회함(select 쿼리)
        //그런데, 한 번의 통신이 있은 후 session이 닫히기 때문에 no session! .LazyInitializationException:failed to lazily initialize a collection of role
        // -> 하나의 작업 단위가 끝날때 까지 유지하도록 트랜잭션처리를 해줘야함

        Optional<Board> result = boardRepository.findById(1L);
        Board board = result.orElseThrow();

        log.info("board={}", board);
        log.info("---------------이전에는 board_image table 조회 안하다가, 이후에 조회쿼리 나감"); //영속컨텍스트에서 데꾸와서 이 log이후에 찍히지는 않음..
        log.info("board.getImageSet={}", board.getImageSet());

    }

    @Test
    void testEntityGraph() {
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();

        log.info("board={}", board);

        //이 경우 @EntityGraph 으로 join처리되서 join 쿼리 나가고 board조회시 image도 다 조회해옴!
    }

    /*
    첨부파일 수정의 경우, 진짜 수정이 아니라 삭제 후 새로운 파일 추가임!
     */

    @Commit //테스트코드의 트랜잭션은 자동롤백이라, db에 반영하고싶으면 commit 어노테이션 필요!
    @Transactional
    @Test
    void testModifyImages() {

        //given
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();

        //when(수정하고파)
        board.clearImage(); // 기존 파일 삭제

        for (int i = 0; i < 5; i++) {
            board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
        }
        Board saved = boardRepository.save(board);

        //then
        log.info("saved={}", saved);
        Assertions.assertThat(saved.getImageSet().size()).isEqualTo(5);

    }

    @Commit
    @Transactional
    @Test
    void testRemoveBoardAll() {

        //Board를 삭제하기위해서는 FK걸린 Reply를 다 삭제 후 삭제 가능(무결성)  Cannot delete or update a parent row: a foreign key constraint fails

        //given (board 1번 게시물 삭제하고 싶음)
        Long bno = 100L;


        replyRepository.deleteByBoard_bno(bno); //먼저 reply다 삭제 후
        boardRepository.deleteById(bno);
    }

    @Test
    @DisplayName("테스트용 더미데이터 생성, 5의배수게시물은 첨부파일 없음")
    void testInsertAll() {

        for (int i = 1; i <= 100; i++) {
            Board board = Board.builder()
                    .title("Titile is ..." + i)
                    .content("Content is ..." + i)
                    .writer("Writer is... star" + i)
                    .build();

            for (int j = 0; j < 3; j++) {
                if (i % 5 == 0) continue;

                board.addImage(UUID.randomUUID().toString(), i + "file" + j + ".jpg");
            }

            boardRepository.save(board);


        }
    }

    /**
     * N+1 문제가 발생 : Board 게시물 1개를 조회하려다 거기 딸린 N개의 곁다리조회발생 -> 성능저하
     * 그래서! N번에 해당하는 쿼리를 모아서 한 번에 실행하는 기능 제공 => @BatchSize
     * 즉 하위 필드 조회 쿼리를 N번 날리는게 아니라 하나의 쿼리에 조건을 in으로 묶어서 보냄!
     * Board가 가지고있는 하위엔티티 필드에서 설정하겠쥬?
     */
    @Test
    @Transactional
    void testSearchImageReplyConut() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//        boardRepository.searchWithAll(null,null,pageable);

        String[] types = {"w", "c"};

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types, "1", pageable);

        log.info("-------------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info("boardListAllDTO={}",boardListAllDTO));

    }

}