package com.distributed.worker.instruct_netty_client;

import com.distributed.domain.InstructInit;
import com.distributed.domain.Parameters;
import com.distributed.util.InstructUtil;
import io.netty.channel.ChannelFuture;
import org.junit.Test;

public class InstructClientTest {
    @Test
    public void testInstruction() throws InterruptedException {
        //启动客户端
        InstructClient instructClient = new InstructClient("Ubuntu001", Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = instructClient.start();

        InstructInit instructInit = InstructUtil.buildInstruction("test", "Ubuntu003");
        //发送信息
        channelFuture.channel().writeAndFlush(instructInit);
        channelFuture.channel().closeFuture().sync(); // 等待关闭
        instructClient.close();
    }

}
