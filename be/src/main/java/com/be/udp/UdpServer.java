package com.be.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.be.dto.SensorMessage;
import com.be.service.KafkaProducerService;
import com.be.service.SensorRealtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;

//UDP 패킷 받기

// UDP 포트 열기
// 패킷 수신
// 메시지 출력

public class UdpServer {

    private final int port;
    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducer;

    public UdpServer(int port, ObjectMapper objectMapper, KafkaProducerService kafkaProducer) {
        this.port = port;
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("[UDP SERVER] Started on port " + port);

            //UDP 패킷을 담는 수신 버퍼 <=> TCP: 스트림 (줄 단위, readLine())
            byte[] buffer = new byte[2048];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());

                try {
                    // 1️. JSON → SensorMessage
                    SensorMessage sm =
                            objectMapper.readValue(msg, SensorMessage.class);

                    // 2️. UDP 메타데이터 보정
                    sm.setProtocol("UDP");
                    sm.setTimestamp(System.currentTimeMillis());

                    // 3️. Kafka로 전달
                    kafkaProducer.sendSensorMessage("sensor-log", sm);
                    System.out.println("[UDP] Message sent to Kafka");

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