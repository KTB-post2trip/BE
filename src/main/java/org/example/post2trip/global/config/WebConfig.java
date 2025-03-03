package org.example.post2trip.global.config;



import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트에 대해 CORS 허용
                .allowedOrigins("http://localhost:5173", " https://post2trip-fe.vercel.app/","http://localhost:8080") // ✅ 허용할 프론트엔드 도메인 추가
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // ✅ 허용할 HTTP 메서드
                .allowedHeaders("*") // ✅ 모든 헤더 허용
                .allowCredentials(true); // ✅ 인증 정보 포함 (JWT 사용 시 필요)
    }
}