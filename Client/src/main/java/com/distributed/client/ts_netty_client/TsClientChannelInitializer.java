package com.distributed.client.ts_netty_client;

import common.codec.ObjDecoder;
import common.codec.ObjEncoder;
import common.codec.TsDecoder;
import common.codec.TsEncoder;
import common.domain.InstructTs;
import common.setting.Parameters;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class TsClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
//        socketChannel.pipeline().addLast(new ObjDecoder(InstructTs.class));
//        socketChannel.pipeline().addLast(new ObjEncoder(InstructTs.class));
        socketChannel.pipeline().addLast(new TsEncoder());
        socketChannel.pipeline().addLast(new TsDecoder(Parameters.tsSize * Parameters.Insert.batchTrans + 8, 4, 4));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new TsClientHandler());
    }
}
