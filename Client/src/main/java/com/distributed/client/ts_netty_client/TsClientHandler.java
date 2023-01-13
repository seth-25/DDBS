package com.distributed.client.ts_netty_client;

import com.distributed.client.insert.InsertAction;
import com.distributed.util.CacheUtil;
import common.setting.Constants;
import common.setting.Parameters;
import common.util.InstructUtil;
import common.domain.InstructTs;
import common.domain.TimeSeries;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;


public class TsClientHandler extends ChannelInboundHandlerAdapter {
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
    static int cnt = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InstructTs instructTs = (InstructTs)msg;
        int instructionStr = instructTs.getInstruction();
        System.out.println(instructionStr);
        ArrayList<TimeSeries> tsList = InsertAction.makeTsList(Parameters.Insert.batchTrans);
        switch (instructionStr) {
            case Constants.InstructionType.INSERT_TS:
                if (tsList.size() > 0) {
                    InstructTs instructTs1 = InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS, tsList);
                    System.out.println("发送时间戳" + CacheUtil.timeSeriesLinkedList.size() + " " + tsList.size());
                    ctx.channel().writeAndFlush(instructTs1);
                    if (tsList.size() != 1000) {
                        System.out.println(tsList.get(tsList.size() - 1));
                    }
                }
                else {
                    InstructTs instructTs1 = InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS_FINISH, null);
                    ctx.channel().writeAndFlush(instructTs1);
                    System.out.println("所有时间戳发送完毕");
                }
                break;
            case Constants.InstructionType.FINISH:
                ctx.close();
                break;
        }

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
