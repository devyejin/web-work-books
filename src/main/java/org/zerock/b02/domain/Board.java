package org.zerock.b02.domain;

import lombok.*;

import javax.persistence.*;

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

    //set으로 수정하는건 좋지 못하므로, 변경가능한 필드만 메서드로 빼기 for 수정
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
