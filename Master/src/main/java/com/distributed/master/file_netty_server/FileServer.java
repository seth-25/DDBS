//package com.distributed.master.file_netty_server;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//
//public class FileServer {
//    private final int PORT;
//    private EventLoopGroup bossGroup = new NioEventLoopGroup();
//    private EventLoopGroup workerGroup = new NioEventLoopGroup();
//    private Channel channel;
//
//    public FileServer(int PORT) {
//        this.PORT = PORT;
//    }
//
//    public void start() {
//        ChannelFuture channelFuture = null;
//        try {
//            ServerBootstrap bootstrap = new ServerBootstrap();
//            bootstrap.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.AUTO_READ, true)
//                    .childHandler(new FileServerChannelInitializer());
//
//            channelFuture = bootstrap.bind(PORT).syncUninterruptibly();
//
//            channel = channelFuture.channel();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//
//        }
//        finally {
//            if (null != channelFuture && channelFuture.isSuccess()) {
//                System.out.println("服务端启动成功");
//            } else {
//                System.out.println("服务端启动失败");
//            }
//        }
//    }
//    public void close() {
//        channel.close();
//        bossGroup.shutdownGracefully();
//        workerGroup.shutdownGracefully();
//    }
//}
