package com.apptivedev.sns.global.config;

import com.apptivedev.sns.auth.interceptor.AuthInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    // 3주차: Interceptor는 생성만으로 동작하지 않으므로
    // WebMvcConfigurer를 통해 인증이 필요한 경로에 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/users", "/users/**", "/posts")
                .excludePathPatterns(
                        "/auth/signup",
                        "/auth/login"
                );
    }
}
