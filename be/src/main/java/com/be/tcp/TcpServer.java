package com.be.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import com.be.dto.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

//TCP 데이터 받기
//TCP 포트 열기
//클라이언트 연결 수락
//메시지 수신
//(지금은) echo 응답
public class TcpServer {

    private final int port;
    private final ObjectMapper objectMapper;

    public TcpServer(int port, ObjectMapper objectMapper) {
        this.port = port;
        this.objectMapper = objectMapper;
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) { //TCP 서버 시작
            System.out.println("[TCP SERVER] Started on port " + port);

            while (true) {
                Socket client = serverSocket.accept(); //클라이언트 접속 대기 (block)
                System.out.println("[TCP SERVER] Client connected: " + client.getInetAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("[TCP SERVER] Received: " + message);

                //네트워크 연결 테스트용 Echo서버
                //     writer.write("Echo: " + message + "\n");
                //     writer.flush();
                // }

                //JSON을 파싱해서 구조화하는 서버
                    try {
                        SensorMessage sensorMessage =
                                objectMapper.readValue(message, SensorMessage.class);

                        System.out.println("[PARSED]");
                        System.out.println(" protocol  : " + sensorMessage.getProtocol());
                        System.out.println(" sourceId  : " + sensorMessage.getSourceId());
                        System.out.println(" type      : " + sensorMessage.getType());
                        System.out.println(" timestamp : " + sensorMessage.getTimestamp());
                        System.out.println(" payload   : " + sensorMessage.getPayload());

                    } catch (Exception e) {
                        System.out.println("[ERROR] Invalid JSON format");
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
