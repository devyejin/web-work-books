package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.BoardDTO;
import org.zerock.b02.repository.BoardRepository;

import javax.transaction.Transactional;
import java.util.Optional;


@Log4j2
@Transactional
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {
        Board board = modelMapper.map(boardDTO, Board.class);
        return boardRepository.save(board).getBno(); //저장하고 저장된 BNO 데려옴
    }

    @Override
    public BoardDTO readOne(Long bno) {
        //조회 작업은 JpaRepository로(단순조회)
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();

        //클라이언트한테는 DTO로
        return modelMapper.map(board,BoardDTO.class);

    }

    @Override
    public void modify(BoardDTO boardDTO) {

        //수정할 객체를 먼저 찾아오기
        Optional<Board> findBoardOP = boardRepository.findById(boardDTO.getBno());
        Board board = findBoardOP.orElseThrow();

        board.change(boardDTO.getTitle(),boardDTO.getContent());

        //저장
        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
        boardRepository.deleteById(bno);
    }
}
