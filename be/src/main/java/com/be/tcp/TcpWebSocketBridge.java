package com.be.tcp;

import com.be.dto.SensorMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

// TCP 데이터 → DTO 변환
// WebSocket으로 push
// TCP 서버는 이 클래스만 호출

@Component
public class TcpWebSocketBridge {

    private final SimpMessagingTemplate messagingTemplate;

    public TcpWebSocketBridge(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(String rawData) {
        SensorMessage msg = new SensorMessage();
        msg.setProtocol("TCP");
        msg.setSourceId("tcp-1");
        msg.setType("DATA");
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPayload(Map.of("value", rawData));

        messagingTemplate.convertAndSend("/topic/data", msg);
    }
}
