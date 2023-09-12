package org.zerock.b01.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BoardDTO {

    private Long bno;

    private String title;

    private String content;

    private String writer;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
