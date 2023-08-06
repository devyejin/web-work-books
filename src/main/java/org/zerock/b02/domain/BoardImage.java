package org.zerock.b02.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class BoardImage implements Comparable<BoardImage> {

    @Id
    private String uuid;
    private String fileName;
    private int ord;

    @ManyToOne //첨부이미지의 경우 Board -> Image 관리하도록 구현 (양방향)
    private Board board;

    @Override
    public int compareTo(BoardImage other) { //자기자신과 매개변수 비교
        return this.ord - other.ord ; // @OneToMany처리에서 순번에 맞게 정렬하기 위해 구현
    }

    public void changeBoard(Board board) { //Board 객체를 나중에 지정할 수 있도록 메서드 정의 -> Board엔티티 삭제시 BoardImage객체 참조 변경할 때 사용
        this.board = board;
    }
}
