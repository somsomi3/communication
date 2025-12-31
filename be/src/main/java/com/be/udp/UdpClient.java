package com.be.udp;

import com.be.dto.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
//TCP/UDP는 전송 방식 차이
//보내기

//서버에 패킷 하나 전송
// 응답 없음

//TCP Client와 차이: 연결 없음, 그냥 던짐
public class UdpClient {

    public static void main(String[] args) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        SensorMessage message = new SensorMessage();
        message.setProtocol("UDP");
        message.setSourceId("sensor-002"); // TCP와 구분
        message.setType("TEMP");
        message.setTimestamp(System.currentTimeMillis());

        Map<String, Object> payload = new HashMap<>();
        payload.put("value", 26.3);
        payload.put("unit", "C");
        message.setPayload(payload);

        String json = objectMapper.writeValueAsString(message);
        byte[] data = json.getBytes();

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");

        DatagramPacket packet =
                new DatagramPacket(data, data.length, address, 6000);

        socket.send(packet);
        socket.close();

        System.out.println("[UDP CLIENT] Sent: " + json);
    }
}