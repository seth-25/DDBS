package com.distributed.worker.ts_netty_server;

import common.codec.ObjDecoder;
import common.codec.ObjEncoder;
import common.codec.TsDecoder;
import common.codec.TsEncoder;
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
        // 前4位是type(int)，长度在偏移为4的位置，长度占4字节
        socketChannel.pipeline().addLast(new TsDecoder(Parameters.tsSize * Parameters.Insert.batchTrans + 8, 4, 4));
        socketChannel.pipeline().addLast(new TsEncoder());
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new TsServerHandler());
//        socketChannel.pipeline().addLast(businessGroup, new TsServerHandler());
    }
}
