package com.distributed.worker.netty_server;

import com.distributed.domain.MyMessage;
import com.distributed.util.ObjDecoder;
import com.distributed.util.ObjEncoder;
import com.distributed.worker.Coordinator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class MyNettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //对象传输处理
        socketChannel.pipeline().addLast(new ObjDecoder(MyMessage.class));
        socketChannel.pipeline().addLast(new ObjEncoder(MyMessage.class));
        // 在管道中添加接收数据实现方法
        socketChannel.pipeline().addLast(new MyNettyServerHandler());
    }
}
