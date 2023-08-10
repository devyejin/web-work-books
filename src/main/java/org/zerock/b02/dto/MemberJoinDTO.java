package org.zerock.b02.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원가입용 DTO와 로그인시 시큐리티가 사용하는 DTO는 별개로 구성해서 처리
 */

@Getter
@Setter
@ToString
public class MemberJoinDTO {

    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;

}
