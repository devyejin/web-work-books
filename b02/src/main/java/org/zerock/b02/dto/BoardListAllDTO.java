package org.zerock.b02.dto;

import lombok.*;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Board, BoardImage, 댓글 개수 모두 반영할 수 있는 DTO
 * Board 목록 뿌려줄 때 한번에 넘김
 */

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListAllDTO {

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;
    private Long replyCount;
    private List<BoardImageDTO> boardImages;
}
