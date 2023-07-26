package org.zerock.b02.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b02.domain.Board;

/**
 * XXXRepositroy가 의존할 인터페이스
 */
public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String types[], String keyword, Pageable pageable);
}
