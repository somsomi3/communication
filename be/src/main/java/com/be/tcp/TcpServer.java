package com.be.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//TCP 데이터 받기
//TCP 포트 열기
//클라이언트 연결 수락
//메시지 수신
//(지금은) echo 응답
public class TcpServer {

    private final int port;

    public TcpServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) { //TCP 서버 시작
            System.out.println("[TCP SERVER] Started on port " + port);

            while (true) {
                Socket client = serverSocket.accept(); //클라이언트 접속 대기 (block)
                System.out.println("[TCP SERVER] Client connected: " + client.getInetAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("[TCP SERVER] Received: " + message);

                    writer.write("Echo: " + message + "\n");
                    writer.flush();
                }

                client.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
