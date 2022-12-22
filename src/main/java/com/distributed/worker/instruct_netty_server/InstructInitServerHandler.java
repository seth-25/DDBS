package com.distributed.worker.instruct_netty_server;

import com.distributed.WorkerAction;
import com.distributed.domain.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class InstructInitServerHandler extends SimpleChannelInboundHandler<InstructInit> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--------------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("连接信息：有1个客户端连接到本服务端。channelId：" + channel.id());
        System.out.println("客户端连接IP和Port：" + channel.remoteAddress());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开连接：" + ctx.channel().remoteAddress());
        System.out.println("--------------------");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InstructInit instructInit) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();
        System.out.println("客户端信息" + instructInit.getInstruction() + " " + instructInit.getDataObject());
        String instructionStr = instructInit.getInstruction();
        switch (instructionStr) {
            case Constants.InstructionType.SEND_SAX_STATISTIC:    // 给Master发送SAX值个数统计
                String hostName = (String) instructInit.getDataObject();    // Master的hostname
                WorkerAction.sendSaxStatics(hostName);
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("fileInfoMap: " + CacheUtil.fileInfoMap);
        ctx.writeAndFlush(new InstructInit("accept"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
