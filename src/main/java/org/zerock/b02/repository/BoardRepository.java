package org.zerock.b02.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b02.domain.Board;
import org.zerock.b02.repository.search.BoardSearch;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardSearch {

    @Query(value="select now()", nativeQuery = true) //nativeQuery : 특정 데이터베이스에서 동작하는 sql을 사용하는 기능
    String getTime();
    @Query("select b from Board b where b.title like concat('%',:keyword,'%')")
    Page<Board> findKeyword(String keyword, Pageable pageable);
}
