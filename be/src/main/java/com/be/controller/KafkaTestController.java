package com.be.controller;

import com.be.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaProducerService producer;

    @GetMapping("/kafka/test")
    public String test() {
        producer.send("sensor-log", "hello kafka");
        return "ok";
    }
}
