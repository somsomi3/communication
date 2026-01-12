package com.be.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.be.dto.SensorMessage;

// Redis는 Service만 알고
// TCP/UDP 서버는 Redis 구조 몰라야 함 -> 완전 분리.

@Service
public class SensorRealtimeService {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public SensorRealtimeService(StringRedisTemplate redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void saveLatest(SensorMessage message) {

        if (message.getSourceId() == null) return;
        if (message.getPayload() == null) return;

        String key = "sensor:latest:" +
                message.getProtocol().toLowerCase() + ":" +
                message.getSourceId();

        // 공통 메타데이터
        redisTemplate.opsForHash().put(key, "protocol", message.getProtocol());
        redisTemplate.opsForHash().put(key, "type", message.getType());
        redisTemplate.opsForHash().put(key, "timestamp", String.valueOf(message.getTimestamp()));

        // payload 전체 저장
        message.getPayload().forEach((k, v) -> {
            if (v != null) {
                redisTemplate.opsForHash().put(key, k, v.toString());
            }
        });

        redisTemplate.expire(key, Duration.ofSeconds(10));
    }
    // 실시간 WebSocket 전송
    
    //프로트콜에 따라, 보내는 topic이 달라짐
    ///topic/data/tcp  → TCP 데이터만
    // /topic/data/udp  → UDP 데이터만
    // /topic/data/ws   → WebSocket 데이터만
    public void pushToWebSocket(SensorMessage message) {
        if (message.getProtocol() == null) return;
        String destination =
            "/topic/data/" + message.getProtocol().toLowerCase();
            
        messagingTemplate.convertAndSend(destination, message);
        
        System.out.println("[WS] Sent to " + destination);
    }

}
