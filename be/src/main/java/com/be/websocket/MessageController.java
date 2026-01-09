package com.be.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.be.dto.SensorMessage;
import com.be.service.KafkaProducerService;

//WebSocket용 컨트롤러

// WebSocket으로 들어온 메시지를 수신하는 입력 전용 컨트롤러
// 수신한 데이터는 Kafka로 전달하고,
// WebSocket 브로드캐스트는 수행하지 않는다.

@Controller
public class MessageController {
    private final KafkaProducerService kafkaProducer;


    public MessageController(KafkaProducerService kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @MessageMapping("/log")
    public void handleLog(SensorMessage message) {
        message.setProtocol("WS");
        message.setTimestamp(System.currentTimeMillis());

        //KafkaProducerService에게 위임
        kafkaProducer.sendSensorMessage("sensor-log", message);
        /* 
                기존의 convertAndSend()는
                "이 서버 인스턴스에 연결된 WebSocket 세션들에게
                지금 수신한 메시지를 즉시 브로드캐스트하라"는 의미였다.
                    WebSocket 입력 → WebSocket 컨트롤러 → 바로 WebSocket 출력
                    (Kafka, DB, 다른 서버를 전혀 거치지 않음)

                Kafka 없으며, 이 서버 메모리 안에서만 끝남, 서버가 1대일 때만 정상 => 서버가 2대 이상일때 문제발생=WebSocket 직접 브로드캐스트의 한계

                [해결책]
                WebSocket은 ‘입력만 담당’
                브로드캐스트는 ‘중앙 파이프(Kafka)’가 담당 */
    }
}