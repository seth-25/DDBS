package com.distributed.master.instruct_netty_server;

import common.setting.Constants;
import common.domain.InstructRun;
import com.distributed.master.version.VersionAction;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import javafx.util.Pair;

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

        String instructionStr = instructRun.getInstruction();
        switch (instructionStr) {
            case Constants.MsgType.SEND_VERSION:    // Worker发来新的版本
                if (!(instructRun.getDataObject() instanceof Pair))
                    throw new RuntimeException("instructRun 类型错误");
                byte[] versionBytes = (byte[]) ((Pair<?, ?>) instructRun.getDataObject()).getKey();
                String workerHostName = (String) ((Pair<?, ?>) instructRun.getDataObject()).getValue();
                VersionAction.changeVersion(versionBytes, workerHostName);  // 更改master的版本
                ctx.channel().writeAndFlush(new InstructRun(Constants.MsgType.FINISH));
                VersionAction.checkWorkerVersion(); // 检查是否有worker需要删除版本
                break;
        }
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