package com.be.udp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.be.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;


//UDP 서버 실행 트리거
//Spring 시작 시
// UDP 서버 실행

//TCP Runner랑 역할동일, 프로토콜만 다름.

@Component
public class UdpRunner implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducer;

    public UdpRunner(
            ObjectMapper objectMapper,
            KafkaProducerService kafkaProducer
    ) {
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void run(String... args) {
        UdpServer server =
                new UdpServer(6000, objectMapper, kafkaProducer);

        new Thread(server::start).start();
        System.out.println("[UDP RUNNER] UDP Server started");
    }
}