package org.zerock.b01.service;

import org.zerock.b01.dto.BoardDTO;

public interface BoardService {

    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    //수정은 필요한 부분만 수정
    void modify(BoardDTO boardDTO);

    void remove(Long bno);
}
