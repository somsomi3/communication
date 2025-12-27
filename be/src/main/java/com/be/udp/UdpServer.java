package com.be.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

//UDP 패킷 받기

// UDP 포트 열기
// 패킷 수신
// 메시지 출력

public class UdpServer {

    private final int port;

    public UdpServer(int port) { this.port = port; }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("[UDP SERVER] Started on port " + port);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); //패킷 하나 수신, 오면 바로 처리

                String msg = new String(packet.getData(), 0, packet.getLength()); //받은 데이터 복원
                System.out.println("[UDP SERVER] Received: " + msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}