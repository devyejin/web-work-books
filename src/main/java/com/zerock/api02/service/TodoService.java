package com.zerock.api02.service;

import com.zerock.api02.dto.PageRequestDTO;
import com.zerock.api02.dto.PageResponseDTO;
import com.zerock.api02.dto.TodoDTO;

import javax.transaction.Transactional;

@Transactional
public interface TodoService {

    Long register(TodoDTO todoDTO);

    TodoDTO read(Long tno); //조회, Service계층에서 변환

    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
