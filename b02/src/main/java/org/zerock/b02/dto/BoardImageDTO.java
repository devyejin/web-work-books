package org.zerock.b02.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardImageDTO {

    private String uuid;
    private String fileName;
    private int ord; //순서
}
