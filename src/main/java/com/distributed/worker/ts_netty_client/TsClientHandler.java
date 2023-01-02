package com.distributed.worker.ts_netty_client;

import com.distributed.domain.*;
import com.distributed.util.FileMsgUtil;
import com.distributed.util.FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

public class TsClientHandler extends ChannelInboundHandlerAdapter {
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("\t\t连接信息：该客户端连接到服务端。channelId：" + channel.id());
        System.out.println("\t\t服务端的的IP和Port：" + channel.remoteAddress());
        System.out.println("\t\t--------------------");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t\t该客户端断开连接" + ctx.channel().localAddress().toString());
        System.out.println("\t\t--------------------");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof InstructTs)) return;
        System.out.println("\t\t收到服务端信息：" + ((InstructTs) msg).getInstruction());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
        System.out.println("--------------------");
    }
}
