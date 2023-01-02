package com.distributed.worker.ts_netty_client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TsClient {
    private final String HOST_NAME;
    private final Integer PORT;
    private EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;
    public TsClient(String HOST_NAME, Integer PORT) {
        this.HOST_NAME = HOST_NAME;
        this.PORT = PORT;
    }

    public void start() {
        ChannelFuture channelFuture = null;


        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TsClientChannelInitializer());

            channelFuture = bootstrap.connect(HOST_NAME, PORT).sync();
            channel = channelFuture.channel();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                System.out.println("\t\tts客户端启动成功");
            } else {
                System.out.println("\t\tts客户端启动失败");
            }
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void close() {
        channel.close();
        group.shutdownGracefully();
    }
}
