package com.be.service;

import com.be.dto.SensorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// 문자열 직접 전송 하지 않고, 
// DTO → JSON → Kafka 
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSensorMessage(String topic, SensorMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            //Kafka는 JSON만 받는다는 규칙
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka JSON 직렬화 실패", e);
        }
    }
}