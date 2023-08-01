package org.zerock.b02.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b02.domain.Board;
import org.zerock.b02.dto.BoardListReplyCountDTO;

/**
 * XXXRepositroy가 의존할 인터페이스
 */
public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String types[], String keyword, Pageable pageable);

    //게시글 목록 조회시 출력해야되는 정보들을 담은 DTO
    Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types,
                                                      String keyword,
                                                      Pageable pageable);

}

