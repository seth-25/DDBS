package com.distributed.worker.file_netty_client;

import common.util.FileMsgUtil;
import com.distributed.util.FileUtil;
import common.domain.FileData;
import common.domain.FileInfo;
import common.domain.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import common.setting.Constants;

public class FileClientHandler extends ChannelInboundHandlerAdapter {
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FileMessage)) return;

        FileMessage fileMessage = (FileMessage)msg;

        int myMessageStep = fileMessage.getStep();

        FileInfo fileInfo = (FileInfo) fileMessage.getDataObject();

        if (myMessageStep == Constants.TransferStep.FILE_RESPONSE) {
            if (fileInfo.getStatus() == Constants.FileStatus.COMPLETE) {
                ctx.flush();
                ctx.close();    // 关闭客户端
                return;
            }
            FileData fileData = FileUtil.readFile(fileInfo.getFilePath(), fileInfo.getFileType(), fileInfo.getReadPosition());
            ctx.writeAndFlush(FileMsgUtil.buildFileData(fileData));
            System.out.println("向客户端传送文件" + fileData.getFileName() + "切片:" + fileData.getBeginPos() + ":" +  fileData.getEndPos());
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
