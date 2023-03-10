package com.distributed.master.instruct_netty_server;

import common.codec.InstructionObjDecoder;
import common.codec.InstructionObjEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class InstructServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new InstructionObjDecoder());
        socketChannel.pipeline().addLast(new InstructionObjEncoder());
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new InstructInitServerHandler());
        socketChannel.pipeline().addLast(new InstructRunServerHandler());
    }
}
