package com.zerock.api02.repository;

import com.zerock.api02.domain.Todo;
import com.zerock.api02.dto.PageRequestDTO;
import com.zerock.api02.dto.TodoDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void testInsert() {

        IntStream.range(1,100).forEach(i -> {

            Todo todo = Todo.builder()
                    .title("Todays Todo... is ... " + i)
                    .dueDate(LocalDate.of(2022, (i % 12) + 1, (i % 30) + 1))
                    .writer("user" + (i % 10))
                    .complete(false)
                    .build();

            todoRepository.save(todo);
        });
    }

    @Test
    void testSearch() {
        //list() 메서드를 TodoSearchImpl에서 구현했지만, 사용은 TodoRepository에서 하겠쥬?!

        //given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .from(LocalDate.of(2022, 10, 01))
                .to(LocalDate.of(2022, 12, 31))
                .build();

        //when
        Page<TodoDTO> list = todoRepository.list(pageRequestDTO);

        //then
        list.forEach(todoDTO -> {
            log.info("todoDTO={}", todoDTO);
        });




    }
}