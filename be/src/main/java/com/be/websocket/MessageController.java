package com.be.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.be.dto.SensorMessage;

//WebSocket용 컨트롤러

@Controller
public class MessageController {

    @MessageMapping("/log")//클라이언트 → /app/log
//WebSocket + STOMP
//메시지 기반
//서버가 주도적으로 메시지 발행 가능
//=> 요청, 응답의 개념이 아니라, 이벤트 처리개념.

    @SendTo("/topic/log")//서버 → /topic/log
    public SensorMessage echo(SensorMessage message) {
        System.out.println("WS 수신 DTO: " + message.getPayload());
        return message;
        // 받은 걸 그대로 다시 보냄
        // Echo 테스트
        // WebSocket 통신 정상 여부 검증용
    }
}
