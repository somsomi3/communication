package com.be.tcp;

import java.io.*;
import java.net.Socket;
// [[센서 역할의 테스트 도구]]

//TCP/UDP는 전송 방식 차이
//TCP로 메시지 보내기

//TCP 서버에 접속
//메시지 전송
//테스트용
//=>직접 TCP 패킷 보내보기 위함

public class TcpClient {

    public void sendMessage(String host, int port, String msg) {
        try (Socket socket = new Socket(host, port)) { //서버 접속

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write(msg + "\n"); //메시지 전송
            writer.flush();

            System.out.println("[TCP CLIENT] Sent: " + msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
