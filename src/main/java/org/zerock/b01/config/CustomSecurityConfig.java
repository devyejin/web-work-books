package org.zerock.b01.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {

    //http 매개변수로 받아서 모든 로직이 여기를 탐 (p680은 모든 로직이 이 필터를 타지만 작업하는게 없어서 시큐리티 적용없이 됨)
    @Bean
    public SecurityFilterChain fiterChain(HttpSecurity http) throws Exception {



        log.info("------------config--------------");
        http.formLogin();

        return http.build(); //build되면서 filterChain 반환
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        log.info("----------web configure------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    //인코더 등록 (다양한 인코더 등록가능한데, 우선 스프링 시큐리티가 제공하는거 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
