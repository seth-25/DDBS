package com.distributed.worker.ts_netty_server;

import com.distributed.domain.*;
import com.distributed.util.InstructUtil;
import com.distributed.worker.insert.InsertAction;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

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
                if (!(instructTs.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructRun 类型错误");
                ArrayList<TimeSeries> clientToWorkerTsList = (ArrayList<TimeSeries>) instructTs.getDataObject();
//                System.out.println("\t收到ts,时间戳: ");
                ctx.writeAndFlush(InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS, null));
                InsertAction.tempStoreTs(clientToWorkerTsList);
                InsertAction.checkStoreTs();
                break;
            case Constants.InstructionType.INSERT_TS_FINISH: // worker收到client的发送ts完成的请求，最后检查没发完的ts
                ctx.writeAndFlush(InstructUtil.buildInstructTs(Constants.InstructionType.FINISH, null));
                InsertAction.finalCheckStoreTs();
                break;

            case Constants.InstructionType.SEND_TS: // worker收到worker的ts，转化成sax，再将sax发送到对应worker
                if (!(instructTs.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructRun 类型错误");
                ArrayList<TimeSeries> workerToWorkerTsList = (ArrayList<TimeSeries>) instructTs.getDataObject();
                ctx.writeAndFlush(new InstructTs("Worker服务端成功接收Worker的ts"));

                ArrayList<Sax> saxes = InsertAction.tsToSax(workerToWorkerTsList);
                InsertAction.tempStoreSax(saxes);
                InsertAction.checkStoreSax();
                break;
            case Constants.InstructionType.SEND_TS_FINISH: // worker收到worker的ts，转化成sax，再将sax发送到对应worker，最后检查没发完的sax
                if (!(instructTs.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructRun 类型错误");
                ArrayList<TimeSeries> workerToWorkerTsListFinal = (ArrayList<TimeSeries>) instructTs.getDataObject();
                ctx.writeAndFlush(new InstructTs("Worker服务端成功接收Worker最后的ts"));

                ArrayList<Sax> saxesFinal = InsertAction.tsToSax(workerToWorkerTsListFinal);
                InsertAction.tempStoreSax(saxesFinal);
                InsertAction.finalCheckStoreSax();
                break;

            case Constants.InstructionType.SEND_SAX: // worker收到sax，存到数据库中
                if (!(instructTs.getDataObject() instanceof ArrayList))
                    throw new RuntimeException("instructRun 类型错误");
                ArrayList<Sax> saxList = (ArrayList<Sax>) instructTs.getDataObject();
                ctx.writeAndFlush(new InstructTs("Worker服务端成功接收Worker的sax"));
                InsertAction.putSax(saxList);
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
