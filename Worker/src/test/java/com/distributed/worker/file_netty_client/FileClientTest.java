package com.distributed.worker.file_netty_client;

import common.setting.Constants;
import common.domain.FileMessage;
import common.setting.Parameters;
import common.util.FileMsgUtil;
import io.netty.channel.ChannelFuture;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileClientTest {


    @Test
    public void testFile() throws InterruptedException, IOException {


        FileClient fileClient = new FileClient("Ubuntu001", Parameters.FileNettyServer.port);
        ChannelFuture channelFuture = fileClient.start();

        File file = new File("./test.txt");
        FileMessage fileMessage = FileMsgUtil.buildFileRequest(file.getAbsolutePath(), file.getName(), Constants.FileType.SAX_STATISTIC, file.length());
        System.out.println(file.getAbsolutePath() + " " + file.getName() + " " + file.length());


        channelFuture.channel().writeAndFlush(fileMessage);
        channelFuture.channel().closeFuture().sync(); // 等待关闭
        fileClient.close();
//        //启动客户端
//        MyNettyClient myNettyClient = new MyNettyClient("Ubuntu001", 2333);
//        ChannelFuture channelFuture = myNettyClient.start();
//
//        MyMessage myMessage = MsgUtil.buildInstruction("test", "Ubuntu003");
//        //发送信息
//        channelFuture.channel().writeAndFlush(myMessage);
//        channelFuture.channel().closeFuture().sync(); // 等待关闭
//        myNettyClient.close();
    }
}
