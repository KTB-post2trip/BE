package org.example.post2trip.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173","https://post2trip-fe.vercel.app/")); // 🔹 허용할 도메인
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 🔹 허용할 HTTP 메서드
        config.setAllowedHeaders(List.of("*")); // 🔹 모든 헤더 허용
        config.setAllowCredentials(true); // 🔹 인증 정보 포함 허용 (JWT 사용 시 필요)

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}