package org.zerock.b02.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Board extends BaseEntity {

    @Id //PK 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //일반적으로는 PK+이메일등 조합해서 사용, AUTO는 쓰지 마라.
    private Long bno;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    @OneToMany(mappedBy = "board", // BoardImage의 누구랑 mapped되는지 => BoardImage 의 board 필드
                cascade = {CascadeType.ALL}, //상위(Board) -> 하위(BoardImage) 영속성 전이
                fetch = FetchType.LAZY,  //지연로딩 하위는 필요 시, 쿼리날림
                orphanRemoval = true) //참조가 끊기면 삭제(즉, board가 제거되면 boardImage도 제거
    @Builder.Default //빌더로 빌드시 특정 값으로 초기화할 때 사용
    private Set<BoardImage> imageSet = new HashSet<>(); //어차피 이름이 겹칠 일이 없어서 Set일까?

    //상위 엔티티가 하위 엔티티를 관리하는 경우, 별도의 JPARepository추가X
    //상위 엔티티에서 하위 엔티티 객체들을 관리하는 기능(메서드)를 추가해서 사용
    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImage() {
        imageSet.forEach(boardImage -> {
            boardImage.changeBoard(null);
        }); // 이미지 -> board 참조 끊고

        this.imageSet.clear(); // board -> 이미지 참조 끊고
    }



    //set으로 수정하는건 좋지 못하므로, 변경가능한 필드만 메서드로 빼기 for 수정
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }



}
