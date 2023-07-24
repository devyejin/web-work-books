package org.zerock.b01.domain;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
@Entity
public class Board extends BaseEntity{ //BaseEntity 있는 필드 상속 받음(reg , mod date)

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false )
    private String title;

    @Column(length = 2000, nullable = false )
    private String content;

    @Column(length = 50, nullable = false )
    private String writer;

    //Entity는 불변이 좋으므로, 변경 가능한 필드의 경우 메서드로 빼놓는걸 추천
    // title, contet 만 변경 가능
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
