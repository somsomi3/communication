package com.be.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.be.dto.SensorMessage;
import com.be.service.SensorRealtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;

//UDP 패킷 받기

// UDP 포트 열기
// 패킷 수신
// 메시지 출력

public class UdpServer {

    private final int port;
    private final ObjectMapper objectMapper;
    private final SensorRealtimeService sensorRealtimeService;

    public UdpServer(
            int port,
            ObjectMapper objectMapper,
            SensorRealtimeService sensorRealtimeService
    ) {
        this.port = port;
        this.objectMapper = objectMapper;
        this.sensorRealtimeService = sensorRealtimeService;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("[UDP SERVER] Started on port " + port);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());

                try {
                    // 1️. JSON → SensorMessage
                    SensorMessage sm =
                            objectMapper.readValue(msg, SensorMessage.class);

                    // 2️. Redis 저장 (실시간)
                    sensorRealtimeService.saveLatest(sm);

                    // 3. WebSocket 실시간 전송
                    sensorRealtimeService.pushToWebSocket(sm);

                    System.out.println("[UDP] Sensor data saved to Redis");

                } catch (Exception e) {
                    System.out.println("[ERROR] Invalid UDP message");
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}