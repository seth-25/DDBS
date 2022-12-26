package com.distributed.worker.instruct_netty_server;

import com.distributed.codec.InstructionObjDecoder;
import com.distributed.codec.InstructionObjEncoder;
import com.distributed.worker.instruct_netty_client.InstructRunClientHandler;
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
