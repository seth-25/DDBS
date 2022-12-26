package com.distributed.worker.ts_netty_server;

import com.distributed.domain.TimeSeries;
import com.distributed.util.CacheUtil;
import com.distributed.util.TsUtil;
import com.distributed.worker.insert.InsertAction;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

public class TsServerHandler extends ChannelInboundHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof TimeSeries)) return;

        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();
        TimeSeries timeSeries = (TimeSeries) msg;
        System.out.println("收到ts,时间戳: " + TsUtil.bytesToLong(timeSeries.getTimeStamp()));
        InsertAction.tempStoreTs(timeSeries);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new String("服务端成功接收ts"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
