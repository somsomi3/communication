package com.be.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


import com.be.dto.SensorMessage;
import com.be.service.SensorDataService;
import com.be.service.SensorRealtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;

//TCP 데이터 받기
//TCP 포트 열기
//클라이언트 연결 수락
//메시지 수신
//(지금은) echo 응답
public class TcpServer {

    private final int port;
    private final ObjectMapper objectMapper;
    private final SensorDataService sensorDataService;
    private final SensorRealtimeService sensorRealtimeService;

    public TcpServer(int port, ObjectMapper objectMapper, SensorDataService sensorDataService, SensorRealtimeService sensorRealtimeService) {
        this.port = port;
        this.objectMapper = objectMapper;
        this.sensorDataService = sensorDataService;
        this.sensorRealtimeService = sensorRealtimeService;
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

                       sensorDataService.saveFromMessage(sm);
                       //+Redis에 저장
                       sensorRealtimeService.saveLatest(sm);


                        System.out.println("[TCP] Sensor data saved");

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