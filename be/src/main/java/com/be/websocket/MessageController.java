package com.be.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.be.dto.SensorMessage;

//WebSocket용 컨트롤러

@Controller
public class MessageController {


    // 클라이언트가 요청하지 않아도
    // 서버가 이벤트 발생 시 WebSocket으로 보내줌.
    
    // 서버가 직접 /topic/log으로 전송
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/log")
    public void handleLog(SensorMessage message) {
        System.out.println("WS DTO 수신: " + message.getPayload());

        messagingTemplate.convertAndSend("/topic/log", message);
    }
}