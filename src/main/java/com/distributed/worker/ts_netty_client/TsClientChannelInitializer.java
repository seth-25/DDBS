package com.distributed.worker.ts_netty_client;

import com.distributed.codec.ObjDecoder;
import com.distributed.codec.ObjEncoder;
import com.distributed.domain.FileMessage;
import com.distributed.domain.InstructTs;
import com.distributed.worker.file_netty_client.FileClientHandler;
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
