package com.zerock.api02.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 기존과 달리, 타임리프 라이브러리(의존성) 주입이 안된 상황인데 html을 호출하면, 스프링이 랜더링을 할 방법이 없어서 에러가 남
 * 그래서! 이제 특정 경로는 스프링MVC에서 SSR이 아닌 일반 파일 경로로 처리하도록 설정
 */
@Configuration
@EnableWebMvc
public class CustomServletConfig  implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/files/**")
                .addResourceLocations("classpath:/static/"); //ex) /files/sample.html 호출시 SSR 방식이 아닌 일반파일 처리함

    }
}
