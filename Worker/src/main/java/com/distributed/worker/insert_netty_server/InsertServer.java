package com.distributed.worker.insert_netty_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class InsertServer extends Thread{

    //todo
    public static long transTime = 0;
    public static long insertTime = 0;
    public static int insertCnt = 0;


    private final int PORT;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    public InsertServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .childHandler(new InsertServerChannelInitializer());

            channelFuture = bootstrap.bind(PORT).syncUninterruptibly();

            channel = channelFuture.channel();
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                System.out.println("\tTs服务端启动成功");
            } else {
                System.out.println("\tTs服务端启动失败");
            }
        }
    }
    public void close() {
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}