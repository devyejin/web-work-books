package org.zerock.b02.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b02.domain.Board;
import org.zerock.b02.domain.QBoard;

import java.util.List;

import static org.zerock.b02.domain.QBoard.board;

/**
 * Querydsl은 Q엔티티 이용!
 */
@Log4j2
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {
        super(Board.class);
    }

    /**
     * Q도메인 객체를 통해 생성되는 쿼리 반환타입이 JPQLQuery
     * 즉, Querydsl 을 통해 JPQL이 생성됨을 알 수 있음!
     * JPQL 실행은 fetch() 메서드를 통해 실행 ( 즉 쿼리 실행하려면 fetch() 호출)
     * fetchCount() 는 쿼리랑 mathching된 수 반환
     *
     * 페이징처리는 QuerydslRepositorySupport 클래스의  applyPagination 메서드를 사용함 <- 찾아보니 Querydsl의 메서드임 그럼 저놈도 상속받은거겠구만
     */
    @Override
    public Page<Board> search1(Pageable pageable) {
        //QBoard를 보면 static으로 QBoard 객체를 제공해줌 그거 사용
        QBoard board = QBoard.board; //Q도메인 객체

        //select ... from board;
        JPQLQuery<Board> query = from(board); //board엔티티로부터 쿼리를 만들겠다.
        log.info("generated jpql query ={}",query );

        query.where(board.title.contains("1")); // where title like...

        //paging
        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> list = query.fetch();
        long count = query.fetchCount();

        //이렇게 간결하게 작성도 가능
//        List<Board> list = from(board)
//                            .where(board.title.contains("1"))
//                            .fetch();





        return null;
    }


}
