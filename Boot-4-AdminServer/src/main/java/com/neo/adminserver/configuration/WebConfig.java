package com.neo.adminserver.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.neo.adminserver.interceptor.SessionInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	// /static/assets를 읽어오지 못하길래 아래와 같이 명시해줌
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }

    
    private final SessionInterceptor sessionInterceptor;

    public WebConfig(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
        .addPathPatterns("/**")  // 인증을 체크할 URL 패턴
        .excludePathPatterns(
                "/**/*.html",  // 모든 .html 요청을 제외
                "/signin.html",  // 로그인 페이지
                "/error",  // 로그인 페이지
                "/signin",         // 로그인 URL
                "/signup",         // 로그인 URL
                "/logout",         // 로그인 URL
                "/loginProcess",  // 로그인 페이지
                "/singup",        // 로그아웃 URL
                "/**/*.css",        // CSS 파일
                "/**/*.js",         // JavaScript 파일
                "/**/images/**"      // 이미지 파일
        );
    }
    
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:4200", "*")
//                .allowedHeaders("*")
//                .allowedMethods("*")
//                .allowCredentials(true);
//    }
}
