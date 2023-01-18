package com.distributed.master.instruct_netty_client;

import common.setting.Parameters;
import common.domain.InstructInit;
import common.util.InstructUtil;
import io.netty.channel.ChannelFuture;
import org.junit.Test;

public class InstructClientTest {
    @Test
    public void testInstruction() throws InterruptedException {
        //启动客户端
        InstructClient instructClient = new InstructClient("Ubuntu002", Parameters.InstructNettyServer.port);
        ChannelFuture channelFuture = instructClient.start();

        InstructInit instructInit = InstructUtil.buildInstructInit(123, "Ubuntu003");
        //发送信息
        channelFuture.channel().writeAndFlush(instructInit);
        channelFuture.channel().closeFuture().sync(); // 等待关闭
        instructClient.close();
    }

}
