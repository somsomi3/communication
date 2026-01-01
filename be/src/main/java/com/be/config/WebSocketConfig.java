package com.be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


//Spring Boot에 STOMP 기반 WebSocket 서버를 켜는 설정 파일

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
// WebSocketMessageBrokerConfigurer: WebSocket/STOMP 관련 설정을 오버라이드하기 위한 인터페이스
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    //registerStompEndpoints: 클라이언트가 처음 접속하는 진입점
        registry.addEndpoint("/ws")
        //WebSocket 연결 주소
                .setAllowedOriginPatterns("*")//CORS 허용
                .withSockJS();//WebSocket 안 되는 환경 대비용 fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
    //configureMessageBroker: STOMP 라우팅 규칙 정의
        registry.setApplicationDestinationPrefixes("/app");
        //클라이언트 → 서버
        // /app/log
        // /app/data

        registry.enableSimpleBroker("/topic");
        // 서버 → 클라이언트
        // /topic/log
        // /topic/data
    }
}
