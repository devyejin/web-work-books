package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private PasswordEncoder passwordEncoder; //생성자 주입 방식이라 final도 @RequiredArgs 어노테이션도 필요 없음, 어차피 특정한 객체 주입이라

    public CustomUserDetailsService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    //1차적으로 username만으로 사용자 정보 로딩 후, 그 정보를 기반으로 패스워드 검증
    //반환되는 UserDetails의 경우 사용자 인증 관련 정보들을 저장하는 역할
    //시큐리티 내부에서 UserDetails 타입 객체를 이용해서 패스워드를 검사하고 사용자 권한을 얻는 방식으로 동작
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        log.info("loadUserByYsername : " + username);

        //UserDetails인터페이스를 구현한 User 클래스를 반환해봅세 (즉, 이런사용자를 반환)
        UserDetails userDetails = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("1111"))
                .authorities("ROLE_USER")
                .build();

        return userDetails;
    }
}
