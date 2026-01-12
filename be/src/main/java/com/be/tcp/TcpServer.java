package com.be.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


import com.be.dto.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.be.service.KafkaProducerService;

public class TcpServer {

    private final int port;
    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducer;

    public TcpServer(int port, ObjectMapper objectMapper, KafkaProducerService kafkaProducer) {
        this.port = port;
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) { //TCP 서버 시작
            System.out.println("[TCP SERVER] Started on port " + port);

            while (true) {
                //TCP 수신
                Socket client = serverSocket.accept(); //클라이언트 접속 대기 (block)
                System.out.println("[TCP SERVER] Client connected: " + client.getInetAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String message;
                while ((message = reader.readLine()) != null) {
                    
                    try {
                        // 1. JSON → SensorMessage
                        SensorMessage sm =
                                objectMapper.readValue(message, SensorMessage.class);

                         // 2. TCP 공통 메타데이터 보정
                        sm.setProtocol("TCP");
                        sm.setTimestamp(System.currentTimeMillis());

                        // 3. Kafka로 전달
                        kafkaProducer.sendSensorMessage("sensor-log", sm);
                        System.out.println("[TCP] Message sent to Kafka");

                    } catch (Exception e) {
                        System.out.println("[ERROR] Invalid TCP message");
                        e.printStackTrace();
                    }
                }

                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}