package org.zerock.b01.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.Board;

public interface BoardSearch {
    
    Page<Board> search1(Pageable pageable); //페이지 처리 기능

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);
}
