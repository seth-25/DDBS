package com.distributed.worker.instruct_netty_client;

import com.distributed.WorkerAction;
import com.distributed.domain.Constants;
import com.distributed.domain.InstructInit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class InstructInitClientHandler extends SimpleChannelInboundHandler<InstructInit> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("连接信息：该客户端连接到服务端。channelId：" + channel.id());
        System.out.println("服务端的的IP和Port：" + channel.remoteAddress());
        System.out.println("--------------------");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("该客户端断开连接" + ctx.channel().localAddress().toString());
        System.out.println("--------------------");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InstructInit instructInit) throws Exception {
        System.out.println("收到服务端" + instructInit.getInstruction());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
        System.out.println("--------------------");
    }
}
