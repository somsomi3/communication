package com.be.service;

import com.be.dto.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "sensor-log", groupId = "sensor-group")
    public void consume(String message) {
        try {
            // 1. Kafka에서 받은 JSON → SensorMessage
            SensorMessage sensorMessage =
                    objectMapper.readValue(message, SensorMessage.class);

            log.info("Kafka → SensorMessage 변환 성공: {}", sensorMessage);

            //// 2. WebSocket으로 브로드캐스트 (여기서만!)
            // 클라이언트 요청 없이도
            // Kafka 이벤트 발생 시
            // 서버가 WebSocket으로 브로드캐스트
            messagingTemplate.convertAndSend("/topic/log", sensorMessage);

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패", e);
        }
    }
}
