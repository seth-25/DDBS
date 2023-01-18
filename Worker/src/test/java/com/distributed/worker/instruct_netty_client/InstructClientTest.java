package com.distributed.worker.instruct_netty_client;

import common.domain.InstructInit;
import common.domain.MsgInsert;
import common.setting.Constants;
import common.setting.Parameters;
import common.util.InstructUtil;
import common.util.MsgUtil;
import io.netty.channel.ChannelFuture;
import org.junit.Test;

public class InstructClientTest {
    @Test
    public void testInstructAndInsert() throws InterruptedException {
        //启动客户端
        InstructClient instructClient = new InstructClient("Ubuntu002", Parameters.InstructNettyServer.port);
        ChannelFuture channelFuture = instructClient.start();

        InstructInit instructInit = InstructUtil.buildInstructInit(123, "Ubuntu003");
        channelFuture.channel().writeAndFlush(instructInit);
        channelFuture.channel().closeFuture().sync(); // 等待关闭
        instructClient.close();
    }

}
