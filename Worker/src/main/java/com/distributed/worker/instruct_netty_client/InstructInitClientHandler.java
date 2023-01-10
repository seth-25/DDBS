package com.distributed.worker.instruct_netty_client;

import common.domain.InstructInit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class InstructInitClientHandler extends SimpleChannelInboundHandler<InstructInit> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t\t--------------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("\t\t连接信息：该客户端连接到服务端。channelId：" + channel.id());
        System.out.println("\t\t服务端的的IP和Port：" + channel.remoteAddress());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t\t该客户端断开连接：" + ctx.channel().localAddress().toString());
        System.out.println("\t\t********************");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InstructInit instructInit) throws Exception {
        System.out.println("\t\t收到服务端消息：" + instructInit.getInstruction());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("\t\t异常信息：\r\n" + cause.getMessage());
        System.out.println("--------------------");
    }
}
