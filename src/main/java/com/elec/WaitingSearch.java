package com.elec;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @Name: wata
 * @Description: 功能描述
 * @Copyright: Copyright (c) 2018
 * @Author: xiongzhenyu
 * @Create Date : 2019/3/26 21:41
 * @Version: 1.0.0
 */
public class WaitingSearch extends Thread{

    private static final int DEVICE_FIND_PORT = 9000;
    private static final int RECEIVE_TIME_OUT = 1500; // 接收超时时间
    private static final int RESPONSE_DEVICE_MAX = 200; // 响应设备的最大个数，防止UDP广播攻击
    private static final byte PACKET_TYPE_FIND_DEVICE_REQ_10 = 0x10; // 搜索请求
    private static final byte PACKET_TYPE_FIND_DEVICE_RSP_11 = 0x11; // 搜索响应
    private static final byte PACKET_TYPE_FIND_DEVICE_CHK_12 = 0x12; // 搜索确认
    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(DEVICE_FIND_PORT);
            byte[] data = new byte[1024];
            DatagramPacket pack = new DatagramPacket(data, data.length);
            System.out.println("客户端上线... 等待搜索...");
            while (true) {
                // 等待主机的搜索
                System.out.println("等待搜索..");
                socket.receive(pack);
                System.out.println("获取到主机的请求数据");
                    byte[] sendData = new byte[1024];
                    DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, pack.getAddress(), pack.getPort());
                    socket.send(sendPack);
                    socket.setSoTimeout(RECEIVE_TIME_OUT);
                    try {
                        socket.receive(pack);
                        InetSocketAddress ipObj=(InetSocketAddress) pack.getSocketAddress();
                        System.out.println("对方ip: "+ipObj.getAddress().getHostAddress()+"  端口:"+ ipObj.getPort());
                            break;
                    } catch (SocketTimeoutException e) {
                    }
                    socket.setSoTimeout(0); // 连接超时还原成无穷大，阻塞式接收
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
