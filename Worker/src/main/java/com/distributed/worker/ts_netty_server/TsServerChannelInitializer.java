package com.distributed.worker.ts_netty_server;

import common.codec.ObjDecoder;
import common.codec.ObjEncoder;
import common.domain.InstructTs;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class TsServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        EventLoopGroup businessGroup = new NioEventLoopGroup(1000);
        //对象传输处理
//        socketChannel.pipeline().addLast(new ObjDecoder(InstructTs.class));
//        socketChannel.pipeline().addLast(new ObjEncoder(InstructTs.class));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new TsServerHandler());
//        socketChannel.pipeline().addLast(businessGroup, new TsServerHandler());
    }
}
