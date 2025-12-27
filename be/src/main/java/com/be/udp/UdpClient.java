package com.be.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//TCP/UDP는 전송 방식 차이
//보내기

//서버에 패킷 하나 전송
// 응답 없음

//TCP Client와 차이: 연결 없음, 그냥 던짐
public class UdpClient {

    public void sendMessage(String host, int port, String msg) {
        try (DatagramSocket socket = new DatagramSocket()) {

            byte[] data = msg.getBytes();
            InetAddress address = InetAddress.getByName(host);

            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);

            System.out.println("[UDP CLIENT] Sent: " + msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}