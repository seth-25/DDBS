package com.distributed.worker.ts_netty_server;

import com.distributed.domain.Constants;
import com.distributed.domain.InstructTs;
import com.distributed.domain.Sax;
import com.distributed.domain.TimeSeries;
import com.distributed.util.CacheUtil;
import com.distributed.util.TsUtil;
import com.distributed.worker.insert.InsertAction;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;

public class TsServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t--------------------");
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("\t连接信息：有1个客户端连接到本服务端。Thread：" + Thread.currentThread().getName());
        System.out.println("\t客户端连接IP和Port：" + channel.remoteAddress());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("\t客户端断开连接：" + ctx.channel().remoteAddress());
        System.out.println("\t********************");
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof InstructTs)) return;
        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();

        InstructTs instructTs = (InstructTs) msg;

        String instructionStr = instructTs.getInstruction();
        System.out.println("\t客户端信息" + instructionStr + " " + Thread.currentThread().getName());
        switch (instructionStr) {
            case Constants.InstructionType.INSERT_TS: // worker收到client的ts，将ts发送到对应worker
                if (!(instructTs.getDataObject() instanceof TimeSeries))
                    throw new RuntimeException("instructRun 类型错误");
                TimeSeries ts = (TimeSeries) instructTs.getDataObject();
                System.out.println("\t收到ts,时间戳: " + TsUtil.bytesToLong(ts.getTimeStamp()));
                ctx.writeAndFlush(new InstructTs("Worker服务端成功接收Client的ts"));
                InsertAction.sendTs(ts);
                break;
            case Constants.InstructionType.SEND_TS: // worker收到worker的ts，转化成sax，再将sax发送到对应worker
                if (!(instructTs.getDataObject() instanceof TimeSeries))
                    throw new RuntimeException("instructRun 类型错误");
                TimeSeries timeSeries = (TimeSeries) instructTs.getDataObject();
                Sax sax = InsertAction.tsToSax(timeSeries);
                ctx.writeAndFlush(new InstructTs("Worker服务端成功接收Worker的ts"));
                InsertAction.sendSax(sax);
                break;
            case Constants.InstructionType.SEND_SAX: // worker收到sax，存到数据库中
                if (!(instructTs.getDataObject() instanceof Sax))
                    throw new RuntimeException("instructRun 类型错误");
                Sax sax1 = (Sax) instructTs.getDataObject();
                ctx.writeAndFlush(new InstructTs("Worker服务端成功接收Worker的sax"));
                InsertAction.putSax(sax1);
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
