package com.distributed.worker.ts_netty_server;

import com.distributed.codec.ObjDecoder;
import com.distributed.codec.ObjEncoder;
import com.distributed.domain.TimeSeries;
import com.distributed.worker.file_netty_server.FileServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class TsServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new ObjDecoder(TimeSeries.class));
        socketChannel.pipeline().addLast(new ObjEncoder(String.class));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new TsServerHandler());
    }
}
