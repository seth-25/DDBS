package com.distributed.client.insert_netty_client;

import common.codec.InsertDecoder;
import common.codec.InsertEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class InsertClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
//        socketChannel.pipeline().addLast(new ObjDecoder(InstructTs.class));
//        socketChannel.pipeline().addLast(new ObjEncoder(InstructTs.class));
        socketChannel.pipeline().addLast(new InsertEncoder());
//        socketChannel.pipeline().addLast(new TsDecoder(Parameters.tsSize * Parameters.Insert.batchTrans + 8, 4, 4));
        socketChannel.pipeline().addLast(new InsertDecoder());
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new InsertClientHandler());
    }
}
