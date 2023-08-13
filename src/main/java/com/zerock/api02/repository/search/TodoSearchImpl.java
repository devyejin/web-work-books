package com.zerock.api02.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.zerock.api02.domain.QTodo;
import com.zerock.api02.domain.Todo;
import com.zerock.api02.dto.PageRequestDTO;
import com.zerock.api02.dto.TodoDTO;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/**
 * Querydsl (QEntity)를 이용해서 동적 쿼리 생성
 */
@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<TodoDTO> list(PageRequestDTO pageRequestDTO) {

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        //기간 검색 있는 경우
        if (pageRequestDTO.getFrom() != null && pageRequestDTO.getTo() != null) {

            BooleanBuilder fromBuilder = new BooleanBuilder();
            fromBuilder.and(todo.dueDate.goe(pageRequestDTO.getFrom())); //쿼리를 동적 생성 todo의 dueDate <= pageRequestDTO.getFrom() 이런식으로..
            fromBuilder.and(todo.dueDate.loe(pageRequestDTO.getTo()));
            query.where(fromBuilder);
        }

        //완료여부 체크?
        if (pageRequestDTO.getCompleted() != null) {
            query.where(todo.complete.eq(pageRequestDTO.getCompleted()));
        }

        if (pageRequestDTO.getKeyword() != null) {
            query.where(todo.title.contains(pageRequestDTO.getKeyword()));
        }

        this.getQuerydsl().applyPagination(pageRequestDTO.getPageable("tno"), query);

        //JPQL은 JPQL의 결과를 DTO로 바로 처리하는 Projection 기능 제공
        //JPQL을 추상화한 Querydsl 또한 Projections.bean() 을 이용해서 한번에 DTO로 처리해준다.
        //사용하기 위해서는 JPQL의 JPQLQuery 객체의 select() 메서드를 이용
        //즉, query날린 결과를 TodoDTO로 반환해줘!
        JPQLQuery<TodoDTO> dtoQuery = query.select(Projections.bean(TodoDTO.class,
                todo.tno,
                todo.title,
                todo.dueDate,
                todo.complete,
                todo.writer
        ));

        List<TodoDTO> list = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();//몇개를 받았는데?

        return new PageImpl<>(list,pageRequestDTO.getPageable("bno"),count);
    }
}
