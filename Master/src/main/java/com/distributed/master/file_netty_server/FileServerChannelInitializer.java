package com.distributed.master.file_netty_server;

import common.codec.FileObjDecoder;
import common.codec.FileObjEncoder;
import com.distributed.domain.FileMessage;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class FileServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new FileObjDecoder(FileMessage.class));
        socketChannel.pipeline().addLast(new FileObjEncoder(FileMessage.class));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new FileServerHandler());
    }
}
