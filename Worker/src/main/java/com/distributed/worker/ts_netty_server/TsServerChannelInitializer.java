package com.distributed.worker.ts_netty_server;//

import common.codec.*;
import common.domain.InstructTs;
import common.setting.Parameters;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TsServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        EventLoopGroup businessGroup = new NioEventLoopGroup(1000);
        //对象传输处理
//        socketChannel.pipeline().addLast(new ObjDecoder(InstructTs.class));
//        socketChannel.pipeline().addLast(new ObjEncoder(InstructTs.class));
        // 长度在偏移为0的位置，长度占4字节
        socketChannel.pipeline().addLast(new TsDecoder());
//        socketChannel.pipeline().addLast(new TsDecoder1());
        socketChannel.pipeline().addLast(new TsEncoder());
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new TsServerHandler());
//        socketChannel.pipeline().addLast(businessGroup, new TsServerHandler());
    }
}
