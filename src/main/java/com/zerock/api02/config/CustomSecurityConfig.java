package com.zerock.api02.config;

import com.zerock.api02.APILoginSuccessHandler;
import com.zerock.api02.filter.APILoginFilter;
import com.zerock.api02.filter.RefreshTokenFilter;
import com.zerock.api02.filter.TokenCheckFilter;
import com.zerock.api02.security.APIUserDetailService;
import com.zerock.api02.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
// 원하는 곳에 @PreAuthorize 어노테이션을 통해 사전 권한 체크 가능, 요청마다 권한을 요구하는지 안하는지 설정을 어노테이션으로 설정
public class CustomSecurityConfig {

    private final APIUserDetailService apiUserDetailService; //filter 계층에서 로그인인층 처리를 해주려면 Authentication Manager가 필요함
    //그런데, Authentication Manager는 APIUserDetailService를 이용해서 생성할 수 있음
    //그래서, filter계층에서 써야하므로, Config에 APIUserDetailService를 주입받은 후 Mnager를 등록

    private final JWTUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { //시큐리티 적용여부 설정

        log.info("==========web configure===================");

        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //정적 리소스는 시큐리티 미적용
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

        log.info("========== configure ===================");

        //필터층에서 인증 처리
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(apiUserDetailService)
                .passwordEncoder(passwordEncoder());

        //Get AuthenticationManager (매니저 얻기, 필터에서 쓰도록)
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        //모든 요청 http는 이 매니저를 통하도록
        http.authenticationManager(authenticationManager);

        //APILoginFilter
        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken"); //APILoginFilter 필터를  '/generateToken' 경로로 지정 즉, url요청시 해당 필터 동작
        apiLoginFilter.setAuthenticationManager(authenticationManager); //해당 필터에 authenticationManager 넣어주기


        //APILoginSuccessHandler 등록
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil); //핸들러에서 jwtUtil을 사용하므로 주입해줘야함!
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler); //등록


        //APILoginFilter의 위치 조정 (지정된 필터 앞에 필터 추가) (추가하는필터, 지정된 필터)  => 추가필터 -> 지정필터순으로 처리 (api 로그인 우선 처리)
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        //api로 시작하는 모든 경로는 TokenCheckFilter 등록
        http.addFilterBefore(tokenCheckFilter(jwtUtil),UsernamePasswordAuthenticationFilter.class);

        //RefreshTokenFilter 호출 처리, 이 필터가 jwt관련 필터들 중 최전방 => refresh -> tokenCheck -> apiLogin
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken",jwtUtil),TokenCheckFilter.class);



        //csrf(cross-site request forgery protection)
        http.csrf().disable(); // csrf 토큰 비활성화
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // STATELESS = 세션을 사용하지 않음

        //cors(corss original resource site
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfiguration());
        });

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","GET","POST","PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Cache-Control","Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;

    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil) {
        return new TokenCheckFilter(jwtUtil);
    }


}
