package com.be.tcp;

import java.net.Socket;
// [[센서 역할의 테스트 도구]]

//TCP/UDP는 전송 방식 차이
//TCP로 메시지 보내기

//TCP 서버에 접속
//메시지 전송
//테스트용
//=>직접 TCP 패킷 보내보기 위함

// TcpClient 센서 시뮬레이터의 역할을 한다.
import com.be.dto.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class TcpClient {

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        SensorMessage message = new SensorMessage();
        message.setProtocol("TCP");
        message.setSourceId("sensor-001");
        message.setType("TEMP");
        message.setTimestamp(System.currentTimeMillis());

        Map<String, Object> payload = new HashMap<>();
        payload.put("value", 23.5);
        payload.put("unit", "C");

        message.setPayload(payload);

        String json = objectMapper.writeValueAsString(message);

        Socket socket = new Socket("localhost", 5000);
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        writer.write(json + "\n");
        writer.flush();

        socket.close();

        System.out.println("[TCP CLIENT] Sent: " + json);
    }
}

