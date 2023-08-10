package org.zerock.b02.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
// 원하는 곳에 @PreAuthorize 어노테이션을 통해 사전 권한 체크 가능, 요청마다 권한을 요구하는지 안하는지 설정을 어노테이션으로 설정
public class CustomSecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // arguments로 들어오는 http 즉, 모든 요청은 이 filterCahin을 거치게됨!
        //DispatcherServlet(front Controller) 호출 전에 filter부터 거친 다음 Dispathcer 호출!
        log.info("-----------configure-------------");

        //  http.formLogin(); // 로그인화면에서 로그인 진행한다는 설정 <-- 시큐리티에서 제공하는 ../login 이 페이지 제공
        //시큐리티 제공 로그인화면은 커스텀이 불가능하므로, 커스텀으로 설정
        //기존엔 /login 페이지로 갔는데 이제는 /member/login 페이지로 리다이렉트됨!
        //시큐리티의 경우, 기본적으로 login(GET,POST), logout 을 다 여기서 처리해버림! ->로그아웃, 로그인(POST)컨트롤러가 필요 없음
        http.formLogin().loginPage("/member/login");


        //스프링시큐리티는 GET을 제외한 POST,DELETE,PUT요청에 대해 CSRF를 체크하도록 되어 있음!
        //이 책 예제에서는 이 코드까지 반영하면 번거롭기때문에 무시하고 진행, CSRF 안한다는 설정 (실무코드는 해야함)
        http.csrf().disable();

        return http.build();
    }

    //static 파일들은 필터를 적용할 필요가없음
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("--------------web configure--------------");

        return (web -> { //web : WebSecurity 객체를 참조, Spring Security 프레임웤이 자동으로 주입시켜줌! (@Bean을 통해 관리하면서)
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        });
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
