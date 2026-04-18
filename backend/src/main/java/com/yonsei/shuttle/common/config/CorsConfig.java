package com.yonsei.shuttle.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS 설정 — SecurityConfig에서 주입받아 사용
 * 프론트엔드와의 CORS (Cross-Origin) 허용 설정
 */
@Configuration
public class CorsConfig {

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin (와일드카드 패턴 사용)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://localhost",
                "https://*.trycloudflare.com",   // Cloudflare Quick Tunnel
                "https://*.cfargotunnel.com"     // Cloudflare Named Tunnel
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}