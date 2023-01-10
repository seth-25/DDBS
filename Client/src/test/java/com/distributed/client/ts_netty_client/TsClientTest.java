package com.distributed.client.ts_netty_client;

import com.distributed.client.insert.InsertAction;
import common.setting.Constants;
import common.domain.InstructTs;
import common.domain.TimeSeries;
import com.distributed.util.CacheUtil;
import common.util.InstructUtil;
import common.util.TsUtil;
import io.netty.channel.ChannelFuture;
import org.junit.Test;
import common.setting.Parameters;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TsClientTest {

    @Test
    public void testTs() {
        for (int i = 0; i < 1000005; i ++) {
            byte[] a = new byte[1024];
            long timeStamp = new Date().getTime() / 1000;
            byte[] b = TsUtil.longToBytes(timeStamp);
            System.out.println("生成时间戳:" + timeStamp + "  hash " + timeStamp % Parameters.tsHash);
            TimeSeries timeSeries = new TimeSeries(a, b);
            CacheUtil.timeSeriesLinkedList.offer(timeSeries);
        }

        TsClient tsClient = new TsClient("Ubuntu002", Parameters.TsNettyServer.port);
        ChannelFuture channelFuture = tsClient.start();

        long startTime = System.currentTimeMillis();

        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS, InsertAction.makeTsList(Parameters.Insert.batchTrans));
        channelFuture.channel().writeAndFlush(instructTs);

        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        tsClient.close();

        long endTime = System.currentTimeMillis();
        System.out.println("执行时间:" + (double)(endTime - startTime) / 1000);
    }


    @Test
    public void testTsThread() throws InterruptedException {
        final int taskCount = 20;    // 线程总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        TsClient tsClient = new TsClient("Ubuntu002", Parameters.TsNettyServer.port);
        ChannelFuture channelFuture = tsClient.start();

        for (int i = 0; i < taskCount; i ++ ) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    byte[] a = new byte[1024];
                    long timeStamp = new Date().getTime()/1000;
//                    timeStamp = 1672573334;
                    byte[] b = TsUtil.longToBytes(timeStamp);
                    System.out.println("时间戳:" + timeStamp + "  hash " + timeStamp % Parameters.tsHash);

                    TimeSeries timeSeries = new TimeSeries(a, b);
                    InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS, timeSeries);
                    channelFuture.channel().writeAndFlush(instructTs);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("关闭");
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
        tsClient.close();
    }

}