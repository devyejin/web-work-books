package org.zerock.b02.service;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.zerock.b02.dto.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardServiceImplTest {

    @Autowired
    private BoardService boardService;

    @Test
    void testRegister() {
        log.info("name={}", boardService.getClass().getName());
   //org.zerock.b02.service.BoardServiceImpl$$EnhancerBySpringCGLIB$$835d420d

        //given
        BoardDTO boardDTO = BoardDTO.builder()
                .title("테스트해볼 타이틀입니당")
                .content("테스트해볼 컨텐츠")
                .writer("테스트해볼 유저")
                .build();
        //when
        Long registeredNum = boardService.register(boardDTO);

        //then
        log.info("bno={}",registeredNum);

    }

    @Test
    void testModify() {
        //given
        BoardDTO boardDTO = BoardDTO.builder().bno(101L).title("수정된 타이틀").content("수정된 컨텐츠").build();

        //when
        boardService.modify(boardDTO);

        //then
        Assertions.assertThat(boardDTO.getTitle()).isEqualTo(boardService.readOne(101L).getTitle());

    }

    @Test
    void testList() {

        //given (테스트용 데이터) 타이틀,내용,작성자 중 키워드 1이 포함된 글 가져오기
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        //when
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        //then
        log.info("responseDTO={}",responseDTO);
    }

    @Test
    void testRegisterWithImages() {

        log.info(boardService.getClass().getName());

        //given
        BoardDTO boardDTO = BoardDTO.builder()
                .title("첨부파일도 있는 게시물 등록 테스트해볼건데!")
                .content("이제 태풍도온다는데 목요일에 꼭 가야하나?!")
                .writer("user00")
                .build();


        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                )
        );

        //when
        Long registeredBno = boardService.register(boardDTO);

        log.info("registeredBno={}",registeredBno);

        //then
        Assertions.assertThat(registeredBno).isNotNull();
    }

    @Test
    @DisplayName("한 건의 데이터를 읽는데 첨부파일 포함해서 조회")
    void testreadOne() {
        //given
        Long bno = 101l;

        //when
        BoardDTO boardDTO = boardService.readOne(bno);

        //then
        log.info("boardDTO={}",boardDTO);
        for(String fileName : boardDTO.getFileNames()) {
            log.info("fileName={}",fileName);
        }
        Assertions.assertThat(boardDTO.getFileNames().size()).isEqualTo(3);
    }

    @Test
    void testModifyAttachedFile() {

        //given (101게시글에 첨부파일 추가 가정)
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("101번 파일에 첨부파일을 추가해볼까해")
                .content("테스트코드 짜는거 넘나 길다, 근데 재밌당")
                .build();

        boardDTO.setFileNames(Arrays.asList( //첨부파일 2개 추가
                UUID.randomUUID()+"_attachedImage.jpg",
                UUID.randomUUID()+"_merong.jpg"
        ));

        //when
        boardService.modify(boardDTO);

        //then (반환되는게 없네 조회해서 확인해야짘ㅋ)
        BoardDTO readBoard = boardService.readOne(101L);
        Assertions.assertThat(readBoard.getFileNames().size()).isEqualTo(2);

        readBoard.getFileNames().forEach(fileName -> log.info("fileName={}",fileName));

    }

    @Test
    void testRemove() {

        //reply가 없는 board(게시물) 삭제 고려

        //given
        Long bno = 97L;

        //when
        boardService.remove(bno);

    }

    @Test
    void testListWithAll() {
        //given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        //when
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        //then
        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO ->  {
            log.info(boardListAllDTO.getBno() + " : " + boardListAllDTO.getTitle());

            if(boardListAllDTO.getBoardImages() != null) {
                for(BoardImageDTO boardImageDTO : boardListAllDTO.getBoardImages()) {
                    log.info("boardImageDTO={}", boardImageDTO);
                }
            }

            log.info("====================================");
        });
    }
}