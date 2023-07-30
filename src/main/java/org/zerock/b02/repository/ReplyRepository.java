package org.zerock.b02.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b02.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    //게시글(bno)에 해당하는 댓글들 페이징 처리 (그래서 bno를 통한 댓글 검색이 잦아 인덱스 처리했군)
    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(@Param("bno") Long bno, Pageable pageable);
}
