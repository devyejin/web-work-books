package org.zerock.b02.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 회원의 경우, Security를 이용하기때문에 즉, UserDetail 이라는 인터페이스 타입을 사용하기 때문에 DTO도 형태가 좀 다름
 */

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User implements OAuth2User { //Security제공 User ( UserDetail 인터페이스 구현한 클래스, 간단하게 UserDetail 타입을 생성할 수 있음)
    //User : 로그인처리시 스프링 시큐리티 사용해서
    //OAuth2User : 소셜로그인처리시

    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;
    private Map<String, Object> props; //소셜 로그인 정보

    public MemberSecurityDTO(String username, String password, String email,
                             boolean del, boolean social,
                             Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities);

        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.del = del;
        this.social = social;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps(); //소셜 로그인 정보 반환
    }

    @Override
    public String getName() {
        return this.mid;
    }
}
