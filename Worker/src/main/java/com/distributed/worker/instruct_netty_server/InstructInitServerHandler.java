package com.distributed.worker.instruct_netty_server;

import com.distributed.util.CacheUtil;
import com.distributed.worker.init.InitAction;
import common.domain.InstructInit;
import common.domain.Sax;
import common.domain.TimeSeries;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import common.setting.Constants;
public class InstructInitServerHandler extends SimpleChannelInboundHandler<InstructInit> {
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
    protected void channelRead0(ChannelHandlerContext ctx, InstructInit instructInit) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();
        System.out.println("\t客户端信息" + instructInit.getInstruction());
        String instructionStr = instructInit.getInstruction();
        switch (instructionStr) {
            case Constants.InstructionType.SEND_SAX_STATISTIC:    // 给Master发送SAX值个数统计
                String hostName = (String) instructInit.getDataObject();    // Master的hostname
                InitAction.setInstructClientToMaster(hostName); // 跟master建立连接
                InitAction.sendSaxStatics(hostName);
                break;
            case Constants.InstructionType.SAX_STATISTIC_FINISH:    // Master收到Worker发来SAX值个数统计
                InitAction.sendSaxStaticsFinish();
                break;
            case Constants.InstructionType.SAX_RANGES:  //  收到Master发送的sax范围，向各Worker分发sax
                if (!(instructInit.getDataObject() instanceof TreeMap))
                    throw new RuntimeException("instructInit 类型错误");
                CacheUtil.workerSaxRanges = (HashMap<String, Pair<byte[],byte[]>>) instructInit.getDataObject();
                InitAction.setInstructClientToWorker(); // 和所有worker建立连接
                InitAction.sendSax();
                break;
            case Constants.InstructionType.TS_RANGES:   // 收到Master发送的ts范围，向各Worker分发ts
                if (!(instructInit.getDataObject() instanceof TreeMap))
                    throw new RuntimeException("instructInit 类型错误");
                CacheUtil.timeStampRanges = (HashMap<String, Pair<Integer, Integer>>) instructInit.getDataObject();
                 InitAction.sendTs();
                break;
            case Constants.InstructionType.SEND_SAX:
                if (!(instructInit.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructInit 类型错误");
                ArrayList<Sax> saxes = (ArrayList<Sax>) instructInit.getDataObject();
                InitAction.putSax(saxes);
                break;
            case Constants.InstructionType.SEND_TS:
                if (!(instructInit.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructInit 类型错误");
                ArrayList<TimeSeries> timeSeriesList = (ArrayList<TimeSeries>) instructInit.getDataObject();
                InitAction.putTs(timeSeriesList);
                break;
        }
        ctx.writeAndFlush(new InstructInit("Worker服务端成功接受指令"));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("fileInfoMap: " + CacheUtil.fileInfoMap);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
