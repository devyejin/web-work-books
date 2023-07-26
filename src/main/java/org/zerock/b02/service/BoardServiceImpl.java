package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.BoardDTO;
import org.zerock.b02.repository.BoardRepository;

import javax.transaction.Transactional;


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
}
