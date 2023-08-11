package com.zerock.api02.domain;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class APIUser { //token 으로 mid, mpw 사용하므로 다른 정보 제외하고 생성

    @Id
    private String mid;
    private String mpw;

    public void changePw(String mpw) {
        this.mpw = mpw;
    }


}
