package com.distributed.master.file_netty_server;

import com.distributed.domain.*;
import com.distributed.util.CacheUtil;
import com.distributed.util.FileMsgUtil;
import com.distributed.util.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

public class FileServerHandler extends ChannelInboundHandlerAdapter {
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
        System.out.println("\t--------------------");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof FileMessage)) return;

        SocketChannel channel = (SocketChannel) ctx.channel();
        String clientHostName = channel.remoteAddress().getHostName();

        FileMessage fileMessage = (FileMessage)msg;
        int fileMessageStep = fileMessage.getStep();

            FileData fileData = (FileData) fileMessage.getDataObject();

            String clientFileName = clientHostName + "/" +fileData.getFileType() + "/" + fileData.getFileName();
            String fileFolder;
            switch (fileData.getFileType()) {
                case Constants.FileType.SAX_STATISTIC:
                    fileFolder = Parameters.MergeSort.saxStatisticFolder;
                    break;
                default:
                    fileFolder = "./";
                    break;
            }

            switch (fileMessageStep) {
                case Constants.TransferStep.FILE_REQUEST:

                    System.out.println("\t客户端"+ clientHostName + "请求传输文件");
                    FileInfo oldFileInfo = CacheUtil.fileInfoMap.get(clientFileName);

                    if (oldFileInfo != null) {   // 之前文件已经传了一部分了
                        if (oldFileInfo.getStatus() == Constants.FileStatus.COMPLETE) {
                            CacheUtil.fileInfoMap.remove(clientFileName);
                        }
                        ctx.writeAndFlush(FileMsgUtil.buildFileResponse(oldFileInfo));
                    }
                    else {  // 初次传文件请求
                        FileUtil.deleteFile(fileFolder, fileData);

                        FileInfo fileInfo = new FileInfo(Constants.FileStatus.BEGIN);
                        fileInfo.setReadPosition(0);
                        fileInfo.setFilePath(fileData.getFilePath());
                        fileInfo.setFileType(fileData.getFileType());
                        FileMessage ansMessage = FileMsgUtil.buildFileResponse(fileInfo);
                        ctx.writeAndFlush(ansMessage);
                        System.out.println("\t服务端向客户端" + clientHostName + "回复传输文件指令");
                    }
                    break;

                case Constants.TransferStep.FILE_DATA:

                    FileInfo fileInfo;
                    if (Constants.FileStatus.COMPLETE == fileData.getStatus()) {
                        fileInfo = new FileInfo(Constants.FileStatus.COMPLETE);
                    }
                    else {
                        fileData.setFileName(clientHostName + ":" + fileData.getFileName()); // 不同client的文件区分开
                        FileUtil.writeFile(fileFolder, fileData);
                        if (Constants.FileStatus.END == fileData.getStatus()) {
                            fileInfo = new FileInfo(Constants.FileStatus.COMPLETE);
                        }
                        else {
                            //文件分片传输指令
                            fileInfo = new FileInfo(Constants.FileStatus.CENTER);
                            fileInfo.setFilePath(fileData.getFilePath());      //客户端文件路径
                            fileInfo.setReadPosition(fileData.getEndPos() + 1);    //读取位置
                            CacheUtil.fileInfoMap.put(clientFileName, fileInfo);
                        }
                    }
                    ctx.writeAndFlush(FileMsgUtil.buildFileResponse(fileInfo));
                    if (fileInfo.getStatus() == Constants.FileStatus.COMPLETE) {
                        CacheUtil.fileInfoMap.remove(clientFileName);
                    }
                    System.out.println("\t服务端向客户端" + clientHostName + "回复传输文件指令");
                    break;

                default:
                    break;
            }



    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("fileInfoMap: " + CacheUtil.fileInfoMap);
        ctx.writeAndFlush(Unpooled.copiedBuffer("\t收到客户端消息", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
