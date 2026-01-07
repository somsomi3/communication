package com.be.controller;

import com.be.dto.SensorMessage;
import com.be.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//버튼 누르면 문자열 보내는 테스트에서
//실제 센서 데이터가 들어왔다고 가정하고 표준 메시지를 하나 만들어서 Kafka로 흘려보내는 시뮬레이터 로 변경함.

//컨트롤러 역할: 입력 → 표준 메시지 생성 → 서비스 호출

@RestController
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaProducerService producer;

    @GetMapping("/kafka/test")
        public String test() {
            SensorMessage message = new SensorMessage();
            message.setProtocol("HTTP");
            message.setSourceId("test-controller");
            message.setType("log");
            message.setTimestamp(System.currentTimeMillis());
            message.setPayload(Map.of("msg", "sensor test message"));

            producer.sendSensorMessage("sensor-log", message);
            return "ok";
        }
    }