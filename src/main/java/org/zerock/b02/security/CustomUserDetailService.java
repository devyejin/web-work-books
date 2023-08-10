package org.zerock.b02.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService 인터페이스 : loadUserByUsername() 메서드 하나만 제공하는 함수형 인터페이스 (함수형 맞나?)
 * - 기존 로그인은 username, pwd 둘 다 체느하는 방식
 * - 스프링 시큐리티가 제공하는 로그인 처리는, username만으로 사용자 정보가 있는지 확인 -> 있으면 불러옴 -> 그 다음 pwd 체크 방식
 *   -> username이 일치하는 정보가 없다면 pwd 체크는 하지도 않음!
 * - (그래서, 요즘 회원가입할 때 이메일주소값 먼저 받은 후 pwd 입력하는형태가 많은 것)
 *
 *
 * 반환 타입 UserDetails : 사용자 인증(Authentication) 관련 정보들을 저장하는 인터페이스 타입
 *  - 제공되는 여러 메서드(기능) 중 getAuthorities() : 사용자자가 가진 모든 인가(Authority) 정보들을 반환
 *  - 대표적으로 UserDetails 인터페이스를 구현한 User 클래스 사용
 */

@Log4j2
@Service
public class CustomUserDetailService implements UserDetailsService {

    private PasswordEncoder passwordEncoder; //생성자 주입 방식

    public CustomUserDetailService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername={}", username);

        //지금은 DB에서 찾아서 반환할 사용자정보가 없으므로, 서버에서 임시로 사용자 만들어서 반환
        //그러면, 이제 내부에서 로그인처리할 때 존재하는 user가 있고 -> 비번이 있으니 로그인처리가 되겠지
        UserDetails userDetails = User.builder()
                .username("user1")
//                .password("1111") 스프링 시큐리티는 기본적으로 PWD 암호화가 필수
                .password(passwordEncoder.encode("1111"))
                .authorities("ROLE_USER")
                .build();

        return userDetails;
    }
}
