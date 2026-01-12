package com.be.service;

import com.be.dto.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final SensorRealtimeService sensorRealtimeService;

    @KafkaListener(topics = "sensor-log", groupId = "sensor-group")
    public void consume(String message) {
        try {
            // 1. Kafka에서 받은 JSON → SensorMessage
            SensorMessage sensorMessage =
                    objectMapper.readValue(message, SensorMessage.class);

                        log.info("[Kafka] SensorMessage 수신: {}", sensorMessage);

            // 2. Redis 저장 (최신값)
            sensorRealtimeService.saveLatest(sensorMessage);

            // 3. WebSocket 브로드캐스트
            sensorRealtimeService.pushToWebSocket(sensorMessage);

            log.info("[Kafka] 실시간 처리 완료");

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패", e);
        }
    }
}
