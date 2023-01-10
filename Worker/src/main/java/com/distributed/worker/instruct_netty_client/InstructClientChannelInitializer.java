package com.distributed.worker.instruct_netty_client;

import common.codec.InstructionObjDecoder;
import common.codec.InstructionObjEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class InstructClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new InstructionObjDecoder());
        socketChannel.pipeline().addLast(new InstructionObjEncoder());
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new InstructInitClientHandler());
        socketChannel.pipeline().addLast(new InstructRunClientHandler());
    }
}
