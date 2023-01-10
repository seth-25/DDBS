package com.distributed.client.ts_netty_client;

import common.codec.ObjDecoder;
import common.codec.ObjEncoder;
import common.domain.InstructTs;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class TsClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new ObjDecoder(InstructTs.class));
        socketChannel.pipeline().addLast(new ObjEncoder(InstructTs.class));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new TsClientHandler());
    }
}
