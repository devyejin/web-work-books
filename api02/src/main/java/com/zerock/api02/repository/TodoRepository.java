package com.zerock.api02.repository;

import com.zerock.api02.domain.Todo;
import com.zerock.api02.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
