package com.be.tcp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

//실행
//Spring Boot가 켜질 때 자동으로 실행
//TCP 서버를 백그라운드 스레드에서 띄움

@Component
public class TcpRunner implements CommandLineRunner {

    private final ObjectMapper objectMapper;

    public TcpRunner(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {

        //JSON → Java 객체 변환은 ObjectMapper가 반드시 필요
        TcpServer server = new TcpServer(5000, objectMapper);
        new Thread(server::start).start();
        System.out.println("[TCP RUNNER] TCP Server started");
    }
}