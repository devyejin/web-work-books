package com.zerock.api02.repository.search;

import com.zerock.api02.dto.PageRequestDTO;
import com.zerock.api02.dto.TodoDTO;
import org.springframework.data.domain.Page;

/**
 * querydsl을 이용한 검색 조건 처리
 */
public interface TodoSearch {

    Page<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
