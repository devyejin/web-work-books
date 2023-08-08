package org.zerock.b02.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b02.domain.Board;
import org.zerock.b02.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardSearch {

    @Query(value="select now()", nativeQuery = true) //nativeQuery : 특정 데이터베이스에서 동작하는 sql을 사용하는 기능
    String getTime();
    @Query("select b from Board b where b.title like concat('%',:keyword,'%')")
    Page<Board> findKeyword(String keyword, Pageable pageable);

    //하위 엔티티(Board_image)를 즉시 로딩하고싶으면 eager(즉시 로딩)을 하면되지만, side effect가 너무 많음
    //조인으로 처리하는게 좋은 방법! => @EntityGraph(jpa지원) 를 이용하면 join 처리를 해줌
    @EntityGraph(attributePaths = {"imageSet"}) //board(상위테이블)에서 같이 로딩해야하는 속성 명시
    @Query("select b from Board b where b.bno =:bno") //=:bno 여기 붙여써줘야함 (주의)
    Optional<Board> findByIdWithImages(@Param("bno") Long bno);
}
