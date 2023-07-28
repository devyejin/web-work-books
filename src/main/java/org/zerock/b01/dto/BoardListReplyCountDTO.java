package org.zerock.b01.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BoardListReplyCountDTO {

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;

    private Long replyCount;
}
