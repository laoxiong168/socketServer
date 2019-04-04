package com.elec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import sun.rmi.runtime.Log;

public class SocketService {

    private static int port = 9999;

    public static void main(String[] args) throws Exception {
        SocketService socketService = new SocketService();
        socketService.oneServer();
    }

    public void oneServer() throws Exception {
        ServerSocket server = new ServerSocket(port);
        System.out.println("服务器已启动.");

        //创建一个ServerSocket在端口5200监听客户请求
        Socket  socket = server.accept();
        //使用accept()阻塞等待客户请求，有客户
        //请求到来则产生一个Socket对象，并继续执行
        System.out.println("客户端ip: "+    socket.getInetAddress()+":"+socket.getPort());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //由Socket对象得到输入流，并构造相应的BufferedReader对象
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        //由Socket对象得到输出流，并构造PrintWriter对象
//            阻塞是接收
//            String acceptData = bufferedReader.readLine();
//            System.out.println("Client:" + acceptData);
        //在标准输出上打印从客户端读入的字符串
        //阻塞式输入
//            String input = br.readLine();
        //从标准输入读入一字符串
        int i = 0;
        while (true) {
            //如果该字符串为 "bye"，则停止循环
            writer.println("----" + i);
            System.out.println("-----" + i++);
            System.out.println("接收到的数据: "+in.readLine());
            //向客户端输出该字符串
            writer.flush();
            //在系统标准输出上打印读入的字符串
//                System.out.println("服务器接收到的数据:" + bufferedReader.readLine());
            Thread.sleep(3000);
            //从Client读入一字符串，并打印到标准输出上
//                input=br.readLine();
            //从系统标准输入读入一字符串
        } //继续循环
   /*         writer.close(); //关闭Socket输出流
            bufferedReader.close(); //关闭Socket输入流
            socket.close(); //关闭Socket
            server.close(); //关闭ServerSocket*/
    }
}