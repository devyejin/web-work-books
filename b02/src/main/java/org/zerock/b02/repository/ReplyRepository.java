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

    /**
     * FK가 걸리기때문에 Board(게시물)을 삭제하려면 참조가 걸린 모든 reply를 삭제해야 board삭제가 가능
     * 그런데, 게시물 작성자가 아닌 타인이 작성한 데이터를 막 삭제하는데에는 문제가 생길 수 있음
     * 그래서, 참조만 끊어놓고 댓글 작성자 db에는 살려놓고 마이페이지에서 볼 수 있도록 하는 경우가 많음(네이버 카페 등)
     */
    void deleteByBoard_bno(Long bno);
}
