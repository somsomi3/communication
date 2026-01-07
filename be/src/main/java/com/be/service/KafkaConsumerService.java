package com.be.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "sensor-log", groupId = "sensor-group")
    public void consume(String message) {
        log.info("Kafka Received: {}", message);
    }
}
