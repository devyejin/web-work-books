package com.zerock.api02.controller;

import com.zerock.api02.dto.PageRequestDTO;
import com.zerock.api02.dto.PageResponseDTO;
import com.zerock.api02.dto.TodoDTO;
import com.zerock.api02.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping(value="/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register(@RequestBody TodoDTO todoDTO) {

        log.info("todoDTO={}",todoDTO);

        Long savedTno = todoService.register(todoDTO);

        return Map.of("tno", savedTno);
    }

    @GetMapping("/{tno}")
    public TodoDTO read(@PathVariable("tno") Long tno) { //@PathVariable : restAPI 에서 url param 읽어들이임

        log.info("read tno={}", tno);
        return todoService.read(tno);
    }

    @GetMapping(value ="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        return todoService.list(pageRequestDTO);
    }

}
