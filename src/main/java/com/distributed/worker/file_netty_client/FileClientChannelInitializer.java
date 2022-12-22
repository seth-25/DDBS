package com.distributed.worker.file_netty_client;

import com.distributed.domain.MyMessage;
import com.distributed.codec.FileObjDecoder;
import com.distributed.codec.FileObjEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class FileClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new FileObjDecoder(MyMessage.class));
        socketChannel.pipeline().addLast(new FileObjEncoder(MyMessage.class));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new FileClientHandler());
    }
}
