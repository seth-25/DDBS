package com.distributed.worker.insert_netty_server;//

import common.codec.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class InsertServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        EventLoopGroup businessGroup = new NioEventLoopGroup(1000);
        //对象传输处理
//        socketChannel.pipeline().addLast(new ObjDecoder(InstructTs.class));
//        socketChannel.pipeline().addLast(new ObjEncoder(InstructTs.class));
        // 长度在偏移为0的位置，长度占4字节
        socketChannel.pipeline().addLast(new InsertDecoder());
        socketChannel.pipeline().addLast(new InsertEncoder());
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new InsertServerHandler());
//        socketChannel.pipeline().addLast(businessGroup, new TsServerHandler());
    }
}
