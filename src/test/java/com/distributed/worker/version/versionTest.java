package com.distributed.worker.version;

import com.distributed.domain.Constants;
import com.distributed.domain.InstructRun;
import com.distributed.domain.Parameters;
import com.distributed.util.CacheUtil;
import com.distributed.util.CoordinatorUtil;
import com.distributed.util.InstructUtil;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

public class versionTest {
    @Test
    public void testVersionChange() {
        // 连接zookeeper
        CoordinatorUtil.coordinator.openCoordinator(Parameters.Zookeeper.connectString);
        // 创建worker临时节点
        if (CoordinatorUtil.coordinator.createNode(CreateMode.EPHEMERAL, Parameters.Zookeeper.workerPath, Constants.WorkerStatus.INIT)) {
            System.out.println("创建临时节点成功");
        }

        CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.CHANGE_VERSION);

        byte[] versionBytes = new byte[40];

        InstructClient instructionClient = new InstructClient(Parameters.hostName, Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = instructionClient.start();
        InstructRun instructrun = InstructUtil.buildInstructRun(Constants.InstructionType.SEND_VERSION, new Pair<>(versionBytes, Parameters.hostName));
        //发送信息
        System.out.println("给" + Parameters.hostName +"发送指令 " + Constants.InstructionType.SEND_VERSION);
        channelFuture.channel().writeAndFlush(instructrun);
        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        instructionClient.close();


    }
}
