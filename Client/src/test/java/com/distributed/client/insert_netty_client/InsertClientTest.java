package com.distributed.client.insert_netty_client;

import com.distributed.client.insert.InsertAction;
import common.domain.MsgInsert;
import common.setting.Constants;
import common.domain.InstructTs;
import common.domain.TimeSeries;
import com.distributed.util.CacheUtil;
import common.util.InstructUtil;
import common.util.MsgUtil;
import common.util.TsUtil;
import io.netty.channel.ChannelFuture;
import org.junit.Test;
import common.setting.Parameters;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertClientTest {

    @Test
    public void startInsert() {  // 发送insert指令

        InsertClient insertClient = new InsertClient("Ubuntu002", Parameters.InsertNettyServer.port);
        ChannelFuture channelFuture = insertClient.start();

        MsgInsert msgInsert = MsgUtil.buildMsgInsert(Constants.MsgType.INSERT_TS, new byte[0]);
        channelFuture.channel().writeAndFlush(msgInsert);

        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        insertClient.close();

    }

}