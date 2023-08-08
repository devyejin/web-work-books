package org.zerock.b02.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b02.domain.Board;
import org.zerock.b02.domain.QBoard;
import org.zerock.b02.domain.QReply;
import org.zerock.b02.dto.BoardImageDTO;
import org.zerock.b02.dto.BoardListAllDTO;
import org.zerock.b02.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

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
//        QBoard board = QBoard.board; //Q도메인 객체
//
//        //select ... from board;
//        JPQLQuery<Board> query = from(board); //board엔티티로부터 쿼리를 만들겠다.
//        log.info("generated jpql query ={}",query );
//
//        query.where(board.title.contains("1")); // where title like...
//
//        //paging
//        this.getQuerydsl().applyPagination(pageable,query);
//
//        List<Board> list = query.fetch();
//        long count = query.fetchCount();


        //--------------------------------------------------------
        //이렇게 간결하게 작성도 가능
//        List<Board> list = from(board)
//                            .where(board.title.contains("1"))
//                            .fetch();


        //------------------------------------------------------
        //or 연산이 필요할 때 BooleanBuilder를 사용
        JPQLQuery<Board> query = from(board);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(board.title.contains("1")); //title like...
        booleanBuilder.or(board.content.contains("1")); //content like...

        query.where(booleanBuilder); //조건 넣어주고
        query.where(board.bno.gt(0L)); // bno가 0보다 큰 레코드 찾아와라  this > args(0L)

        List<Board> list = query.fetch();

        return null;
    }

    /**
     * 검색조건 types 에는 제목(t), 내용(c), 작성자(w) 로 구성
     */
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;//Querydsl을 사용하기 위해 우선 Q엔티티를 가져오고
        JPQLQuery<Board> query = from(board);

        //검색 조건(types)와 키워드가 있다면
        if((types != null && types.length >0) && keyword != null) {

            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        //bno>0 조건
        query.where(board.bno.gt(0L)); // this > 0(bno args)

        //paging처리
        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> list = query.fetch();
        long count = query.fetchCount();

        //반환타입이 Page<Board> 이기 때문에 Querydsl에서 직접처리해야 하는 불편함이 존재
        //Spring Data JPA 에서는 PageImpl이라는 클래스를 제공해서 3개의 파라미터로 Page<T>를 생성할 수 있다
        //- List<T> : 실제 목록 데이터 , Pageable : 페이지 관련 정보를 가진 객체 , long : 전체 개수

        // -> 아 여기서 반환된 이 3가지 정보들을 다 보내줘야하는데 한번에 보내주도록 묶어주는 객체!
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        //Board 데이터를 가져올 때 Reply데이터도 가져와야하는데 Reply->Board 단방향임
        //JPQL의 inner join을 이용 가능! 댓글이 없어도 리스트는 가져와야하므로 outer join 이용
        //querydsl 이용

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));

        query.groupBy(board); //게시물 당 으로 처리해야하니까 groupBy처리
        // <--- 여기까진 기본 쿼리 준비

        //pagination
        if( (types != null && types.length >0) && keyword != null) { //검색타입 고르고, 키워드 넣으니까

            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type : types) {

                switch(type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or((board.content.contains(keyword)));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

//        bno > 0
        query.where(board.bno.gt(0L));


        //JPQL은 JPQL의 결과를 DTO로 바로 처리하는 Projection 기능 제공
        //JPQL을 추상화한 Querydsl 또한 Projections.bean() 을 이용해서 한번에 DTO로 처리해준다.
        //사용하기 위해서는 JPQL의 JPQLQuery 객체의 select() 메서드를 이용
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount")
        ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch(); // 반환해줘야할 것 dtoList

        long count = dtoQuery.fetchCount(); // <--- 반환된 데이터 갯수(페이지처리할 때 필요)

        //반환타입이 Page<Board> 이기 때문에 Querydsl에서 직접처리해야 하는 불편함이 존재
        //Spring Data JPA 에서는 PageImpl이라는 클래스를 제공해서 3개의 파라미터로 Page<T>를 생성할 수 있다
        //- List<T> : 실제 목록 데이터 , Pageable : 페이지 관련 정보를 가진 객체 , long : 전체 개수

        // -> 아 여기서 반환된 이 3가지 정보들을 다 보내줘야하는데 한번에 보내주도록 묶어주는 객체!

        return new PageImpl<>(dtoList, pageable, count);

    }

    /**
     * List<Tuple>을 사용하는게 Projections을 사용하는 것 보다 번거롭지만 커스터마이징 가능하다는 장점
     *
     * Projection은 누구인가?
     * - JPQL은 JPQL의 결과를 DTO로 바로 처리하는 Projection 기능 제공
     * - JPQL을 추상화한 Querydsl 또한 Projections.bean() 을 이용해서 한번에 DTO로 처리해준다.
     * - 사용하기 위해서는 JPQL의 JPQLQuery 객체의 select() 메서드를 이용
     *
     * => 즉, JPQL 수행결과를 Projection을 통해 DTO로 바꿀 수도 있고 List<Tuple>을 이용할 수도 있다
     *    밑에 보면 tuple을 map함수를 통해 list로 변환해서 dto에 담아주고 있드아.
     */
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board)); //left join

        //검색 처리 (먼저, join으로 테이블 묶어놓은 다음 검색)
        if( (types!=null && types.length >0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            boardJPQLQuery.where(booleanBuilder);
         }

        boardJPQLQuery.groupBy(board); //게시물별로 묶어주고

        getQuerydsl().applyPagination(pageable,boardJPQLQuery); //paging

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());

        //파이썬과 마찬가지로 자바의 tuple데이터구조도 변경불가능한 데이터 담을 때 사용(조회니까 사용하는 듯)
        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Board board1 = (Board) tuple.get(board);
            //jpa query 결과가 tuple로 반환되는데 result tuple에서 index값으로 가져온다
            //위에 쿼리보면, 특정번호(bno)의 replycount가져오랬으니까
            long replyCount = tuple.get(1, Long.class); //1번 인덱스에 해당되는 튜플을 가져옴

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //BoardImage를 BoardImageDTO 처리할 부분
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted().map(boardImage -> BoardImageDTO.builder()
                    .uuid(boardImage.getUuid())
                    .fileName(boardImage.getFileName())
                    .ord(boardImage.getOrd()).
                    build()).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS); //처리된 BoardImageDTO들 추가

            return dto;

        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount(); // Paging처리할 때 totalCount 필요

        return new PageImpl<>(dtoList,pageable,totalCount);

//        boardList.forEach(board1 -> {
//            log.info("board.bno={}",board1.getBno());
//            log.info("board.imageSet={}",board1.getImageSet());
//            log.info("=============================");
//        });
//        return null;
    }


}
