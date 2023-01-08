package com.distributed.worker;

import com.distributed.domain.Constants;
import com.distributed.domain.InstructRun;
import com.distributed.domain.Parameters;
import com.distributed.util.InstructUtil;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

public class WorkerTest {

    public static void main(String[] args) {
        InstructClient instructionClient = new InstructClient(Parameters.hostName, Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = instructionClient.start();
        InstructRun instructrun = InstructUtil.buildInstructRun(Constants.InstructionType.SEND_VERSION, new Pair<>(new byte[10], Parameters.hostName));
        //发送信息
        System.out.println("给" + Parameters.hostName +"发送指令 " + Constants.InstructionType.SEND_VERSION);
        channelFuture.channel().writeAndFlush(instructrun);
        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        instructionClient.close();
        System.out.println("send_edit");
    }
}
