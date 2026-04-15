package com.yonsei.shuttle.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP WebSocket 설정
 * 실시간 셔틀 위치를 프론트엔드로 푸시할 WebSocket(STOMP)
 *
 * - 연결 엔드포인트: ws://{host}/ws
 * - 구독 Prefix   : /topic/**  (예: /topic/shuttle-location/1)
 * - 발행 Prefix   : /app/**    (클라이언트 → 서버)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버 → 클라이언트 (구독용)
        registry.enableSimpleBroker("/topic");
        // 클라이언트 → 서버 (발행용)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "http://localhost"
                )
                .withSockJS();  // SockJS fallback 지원
    }
}