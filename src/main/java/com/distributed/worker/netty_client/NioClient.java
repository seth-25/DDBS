package com.distributed.worker.netty_client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 客户端
 */
public class NioClient{
    static final Integer PORT = 2333;
    SocketChannel socketChannel;
    public NioClient(String HOST_NAME) {
        try {
            //1. 打开通道
            this.socketChannel = SocketChannel.open();
            //2. 设置连接IP和端口号
            this.socketChannel.connect(new InetSocketAddress(HOST_NAME,PORT));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeNioClient() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write() throws IOException{
        //3. 写出数据
        socketChannel.write(ByteBuffer.wrap("range".getBytes(StandardCharsets.UTF_8)));
        //4. 读取Worker写回的数据
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        int read = socketChannel.read(allocate);
        System.out.println("Worker消息："+new String(allocate.array(),0,read,StandardCharsets.UTF_8));
    }
}

