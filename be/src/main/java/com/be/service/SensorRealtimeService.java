package com.be.service;

import java.time.Duration;
import java.util.Map;

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

        String key = "sensor:latest:" +  message.getProtocol().toLowerCase() + ":" + message.getSourceId();
        Map<String, Object> payload = message.getPayload();

        if (payload == null) return;

        if (payload.get("value") != null)
            redisTemplate.opsForHash().put(key, "value", payload.get("value").toString());

        if (payload.get("unit") != null)
            redisTemplate.opsForHash().put(key, "unit", payload.get("unit").toString());

        redisTemplate.opsForHash().put(key, "type", message.getType());
        redisTemplate.opsForHash().put(key, "timestamp", String.valueOf(message.getTimestamp()));

        //TTL=데이터의 유효기간 설정 (실시간 데이터의 생명주기)
        redisTemplate.expire(key, Duration.ofSeconds(10));
    }
    // 실시간 WebSocket 전송
    public void pushToWebSocket(SensorMessage message) {
        messagingTemplate.convertAndSend("/topic/data", message);
    }
}
