package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.*;
import org.zerock.b02.repository.BoardRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Log4j2
@Transactional
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {
//        Board board = modelMapper.map(boardDTO, Board.class);
//        return boardRepository.save(board).getBno(); //저장하고 저장된 BNO 데려옴

//        modelMapper를 이용하다, 첨부파일에서는 직접 만든 메서드를 이용 (default라 바로 사용)
        Board board = dtoToEntity(boardDTO); //내부에서 dto에 첨부파일있으면 알아처 처리(구현해놨으니까)

        return boardRepository.save(board).getBno(); //등록된 게시물을 보여준다거나, 후처리를 위해 bno return

    }

    @Override
    public BoardDTO readOne(Long bno) {
//        //조회 작업은 JpaRepository로(단순조회)
//        Optional<Board> result = boardRepository.findById(bno);
//        Board board = result.orElseThrow();
//
//        //클라이언트한테는 DTO로
//        return modelMapper.map(board, BoardDTO.class);

        //board_image까지 읽을 수 있는 조인처리된 findByWithImages() 이용
        Optional<Board> result = boardRepository.findByIdWithImages(bno);
        Board board = result.orElseThrow();

        return entityToDTO(board);

    }

    @Override
    public void modify(BoardDTO boardDTO) {

        //수정할 객체를 먼저 찾아오기
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());
        Board board = result.orElseThrow();

        board.change(boardDTO.getTitle(), boardDTO.getContent());

        //첨부파일 수정의 경우, 기존 첨부파일 삭제 후, 추가임! (title,cotent와 달리)
        board.clearImage(); //기존 파일 삭제

        if(boardDTO.getFileNames() != null) { //새로운 파일 추가
            for(String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                board.addImage(arr[0],arr[1]);
            }
        }

        //저장
        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
        boardRepository.deleteById(bno);
    }


    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {

        //이제 단순crud아님 querydsl활용
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        //Page -> List<BoardDTO> 변환
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        log.info("pageRequestDTO={}",pageRequestDTO);  //<--여기까지는 잘 받음

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);


        PageResponseDTO<BoardListReplyCountDTO> pageResponseDTO = PageResponseDTO.<BoardListReplyCountDTO>withAll() //<--여기서 문제가 있는거 같은데
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();

        log.info("pageResponseDTO={}",pageResponseDTO);

        return pageResponseDTO;
    }


    /**
     * 첨부파일, 댓글까지 다 조회처리해주는 메서드
     */
    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types, keyword, pageable);

        //Page객체를 PageResponseDT로 반환!

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();

    }

}
