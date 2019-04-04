package com.elec;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

/**
 * 设备搜索类 Created by zjun on 2016/9/3.
 */
public  class DeviceSearcher extends Thread {

    private static final String TAG = DeviceSearcher.class.getSimpleName();

    private static final int DEVICE_FIND_PORT = 9000;
    private static final int RECEIVE_TIME_OUT = 1500; // 接收超时时间
    private static final int RESPONSE_DEVICE_MAX = 200; // 响应设备的最大个数，防止UDP广播攻击
    private static final byte PACKET_TYPE_FIND_DEVICE_REQ_10 = 0x10; // 搜索请求
    private static final byte PACKET_TYPE_FIND_DEVICE_RSP_11 = 0x11; // 搜索响应
    private static final byte PACKET_TYPE_FIND_DEVICE_CHK_12 = 0x12; // 搜索确认

    private static final byte PACKET_DATA_TYPE_DEVICE_NAME_20 = 0x20;
    private static final byte PACKET_DATA_TYPE_DEVICE_ROOM_21 = 0x21;

    private DatagramSocket hostSocket;

    private byte mPackType;
    private String mDeviceIP;

    DeviceSearcher() {
    }

    @Override
    public void run() {
        try {
            hostSocket = new DatagramSocket();
            // 设置接收超时时间
            hostSocket.setSoTimeout(RECEIVE_TIME_OUT);
            //设置发送数据的大小
            byte[] sendData = new byte[1024];
            //广播地址
            InetAddress broadIP = InetAddress.getByName("255.255.255.255");
            //设置广播地址  广播端口 创建对象
            DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, broadIP, DEVICE_FIND_PORT);
            //发送三次
            for (int i = 0; i < 3; i++) {
                // 发送搜索广播
                mPackType = PACKET_TYPE_FIND_DEVICE_REQ_10;
                //插入数据
                sendPack.setData(packData(i + 1));
                //发送数据`
                System.out.println("第 "+i+"次发送搜索请求");
                hostSocket.send(sendPack);

                // 监听来信
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePack = new DatagramPacket(receiveData, receiveData.length);
                try {
                    // 最多接收200个，或超时跳出循环
                    int rspCount = RESPONSE_DEVICE_MAX;
                    while (rspCount-- > 0) {
                        //设置接收的数据
                        receivePack.setData(receiveData);
                        //接受数据
                        hostSocket.receive(receivePack);
                        if (receivePack.getLength() > 0) {
                            mDeviceIP = receivePack.getAddress().getHostAddress();
//                                Log.i(TAG, "@@@zjun: 设备上线：" + mDeviceIP);
                                // 发送一对一的确认信息。使用接收报，因为接收报中有对方的实际IP，发送报时广播IP
                                mPackType = PACKET_TYPE_FIND_DEVICE_CHK_12;
                                receivePack.setData(packData(rspCount)); // 注意：设置数据的同时，把recePack.getLength()也改变了
                                hostSocket.send(receivePack);
                        }
                    }
                } catch (SocketTimeoutException e) {
                }
//                Log.i(TAG, "@@@zjun: 结束搜索" + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hostSocket != null) {
                hostSocket.close();
            }
        }

    }



    /**
     * 打包搜索报文 协议：$ + packType(1) + sendSeq(4) + [deviceIP(n<=15)] packType - 报文类型 sendSeq - 发送序列 deviceIP - 设备IP，仅确认时携带
     */
    private byte[] packData(int seq) {
        byte[] data = new byte[1024];
        int offset = 0;

        data[offset++] = '$';

        data[offset++] = mPackType;

        seq = seq == 3 ? 1 : ++seq; // can't use findSeq++
        data[offset++] = (byte) seq;
        data[offset++] = (byte) (seq >> 8);
        data[offset++] = (byte) (seq >> 16);
        data[offset++] = (byte) (seq >> 24);

        if (mPackType == PACKET_TYPE_FIND_DEVICE_CHK_12) {
            byte[] ips = mDeviceIP.getBytes(Charset.forName("UTF-8"));
            System.arraycopy(ips, 0, data, offset, ips.length);
            offset += ips.length;
        }

        byte[] result = new byte[offset];
        System.arraycopy(data, 0, result, 0, offset);
        return result;
    }


    /**
     * 设备Bean 只要IP一样，则认为是同一个设备
     */
    public static class DeviceBean {

        String ip;      // IP地址
        int port;       // 端口
        String name;    // 设备名称
        String room;    // 设备所在房间

        @Override
        public int hashCode() {
            return ip.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DeviceBean) {
                return this.ip.equals(((DeviceBean) o).getIp());
            }
            return super.equals(o);
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }
    }
}