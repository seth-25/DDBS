package com.distributed.worker.netty_client;

import com.distributed.domain.Constants;
import com.distributed.domain.MyMessage;
import com.distributed.util.MsgUtil;
import com.distributed.worker.netty_client.MyNettyClient;
import io.netty.channel.ChannelFuture;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class MyNettyClientTest {
    @Test
    public void testInstruction() throws InterruptedException {

        //启动客户端
        MyNettyClient myNettyClient = new MyNettyClient("Ubuntu001", 6778);
        ChannelFuture channelFuture = myNettyClient.start();

        MyMessage myMessage = MsgUtil.buildInstruction("test", "Ubuntu003");
        //发送信息
        channelFuture.channel().writeAndFlush(myMessage);
        channelFuture.channel().closeFuture().sync(); // 等待关闭
        myNettyClient.close();
    }

    @Test
    public void testFile() throws InterruptedException, IOException {


        MyNettyClient myNettyClient = new MyNettyClient("Ubuntu001", 6778);
        ChannelFuture channelFuture = myNettyClient.start();

        File file = new File("./test.txt");
        MyMessage myMessage = MsgUtil.buildFileRequest(file.getAbsolutePath(), file.getName(), Constants.FileType.SAX_STATISTIC, file.length());
        System.out.println(file.getAbsolutePath() + " " + file.getName() + " " + file.length());

        channelFuture.channel().writeAndFlush(myMessage);
        channelFuture.channel().closeFuture().sync(); // 等待关闭
        myNettyClient.close();
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
