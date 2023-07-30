package org.zerock.b02.domain;

import lombok.*;

import javax.persistence.*;

/**
 * 댓글은 '게시물 번호'를 통해서 사용되는 경우가 많음 ex. 게시물당 댓글 수, 게시물의 댓글 목록
 * 쿼리 조건으로 자주 사용되는 칼럼에 인덱스를 생성하면 좋음 => @Table 어노테이션에 지정 가능
 */

//@Index 를 이용해 인덱스 생성, name : 인덱스명, columnList : 어떤 칼럼에 대해 인덱스 생성할건지
//이제 board_bno 칼럼을 이용한 검색은 인덱스가 있어서 속도가 빠름!
@Table(name = "Reply", indexes = {
        @Index(name="idx_reply_board_bno", columnList = "board_bno")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
//@ToString(exclude = "board")
@ToString
@Entity
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY) //필요시
    private Board board; // 테이블 칼럼명은 board_bno

    private String replyText;

    private String replyer;
}
