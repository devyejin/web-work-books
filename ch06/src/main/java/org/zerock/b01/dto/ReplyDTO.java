package org.zerock.b01.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReplyDTO {

    private Long rno;

    @NotNull
    private Long bno; //게시물 번호

    @NotEmpty
    private String replyText;

    @NotEmpty
    private String replyer;
    private LocalDateTime regDate, modDate;

}
