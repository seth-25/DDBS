package com.distributed.worker.instruct_netty_server;

import com.distributed.domain.*;
import com.distributed.worker.insert.InsertAction;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;
import java.util.TreeMap;

public class InstructRunServerHandler extends SimpleChannelInboundHandler<InstructRun> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t--------------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("\t连接信息：有1个客户端连接到本服务端。channelId：" + channel.id());
        System.out.println("\t客户端连接IP和Port：" + channel.remoteAddress());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t客户端断开连接：" + ctx.channel().remoteAddress());
        System.out.println("\t********************");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InstructRun instructRun) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();
        System.out.println("\t客户端信息" + instructRun.getInstruction());
        String instructionStr = instructRun.getInstruction();
        switch (instructionStr) {
            case Constants.InstructionType.SEND_TS: // worker收到ts，转化成sax，再将sax发送到对应机器
                if (!(instructRun.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructRun 类型错误");
                ArrayList<TimeSeries> timeSeries = (ArrayList<TimeSeries>) instructRun.getDataObject();
                ArrayList<Sax> saxes = InsertAction.tsToSax(timeSeries);
                InsertAction.sendSax(saxes);
                break;
            case Constants.InstructionType.SEND_SAX: // worker收到sax，存到数据库中
                if (!(instructRun.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructRun 类型错误");
                ArrayList<Sax> saxList = (ArrayList<Sax>) instructRun.getDataObject();
                InsertAction.putSax(saxList);
                break;

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("fileInfoMap: " + CacheUtil.fileInfoMap);
        ctx.writeAndFlush(new InstructRun("accept"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
