package org.zerock.b02.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 게시글 목록 출력시, 목록에 댓글 수 표시할 때 필요해서 새롭게 만든 DTO
 */

@ToString
@Getter
@Setter
public class BoardListReplyCountDTO {
    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;

    private Long replyCount;
}
