package com.be.udp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//UDP 서버 실행 트리거
//Spring 시작 시
// UDP 서버 실행

//TCP Runner랑 역할동일, 프로토콜만 다름.

@Component
public class UdpRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        UdpServer server = new UdpServer(6000);
        new Thread(server::start).start();
        System.out.println("[UDP RUNNER] UDP Server started");
    }
}