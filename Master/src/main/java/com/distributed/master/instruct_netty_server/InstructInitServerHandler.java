package com.distributed.master.instruct_netty_server;

import common.setting.Constants;
import common.domain.InstructInit;
import com.distributed.util.CacheUtil;
import common.util.InstructUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.TreeMap;

public class InstructInitServerHandler extends SimpleChannelInboundHandler<InstructInit> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--------------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("\t连接信息：有1个客户端连接到本服务端。channelId：" + channel.id());
        System.out.println("\t客户端连接IP和Port：" + channel.remoteAddress());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t客户端断开连接：" + ctx.channel().remoteAddress());
        System.out.println("--------------------");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InstructInit instructInit) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();
        System.out.println("\t客户端信息" + instructInit.getInstruction());
        String instructionStr = instructInit.getInstruction();
        switch (instructionStr) {
            case Constants.MsgType.SAX_STATISTIC:    // Worker发来SAX值个数统计
                if (!(instructInit.getDataObject() instanceof TreeMap))
                    throw new RuntimeException("instructInit 类型错误");
                TreeMap<String, Long> cntSaxes = (TreeMap<String, Long>) instructInit.getDataObject();
                CacheUtil.cntWorkerSaxes.add(cntSaxes);
                channel.writeAndFlush(InstructUtil.buildInstructInit(Constants.MsgType.SAX_STATISTIC_FINISH, null));
                break;
        }
        ctx.writeAndFlush(new InstructInit("master收到worker消息"));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("fileInfoMap: " + CacheUtil.fileInfoMap);
//        ctx.writeAndFlush(new InstructInit("master收到worker消息"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
