package com.distributed.client.ts_netty_client;

import com.distributed.client.insert.InsertAction;
import common.domain.MsgTs;
import common.setting.Constants;
import common.domain.InstructTs;
import common.domain.TimeSeries;
import com.distributed.util.CacheUtil;
import common.util.InstructUtil;
import common.util.MsgUtil;
import common.util.TsUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import org.junit.Test;
import common.setting.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TsClientTest {



    @Test
    public void testTsBytebuf() {  // worker不应答
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

        byte[] tsList = InsertAction.makeTsListByte(Parameters.Insert.batchTrans);
        while(tsList.length > 0) {
            System.out.println("发送时间戳" + CacheUtil.timeSeriesLinkedList.size() + " " + tsList.length / Parameters.tsSize);
            MsgTs msgTs = MsgUtil.buildMsgTs(Constants.InstructionType.INSERT_TS, tsList);
            channelFuture.channel().writeAndFlush(msgTs);
            tsList = InsertAction.makeTsListByte(Parameters.Insert.batchTrans);
        }
        channelFuture.channel().writeAndFlush(tsList);
        System.out.println("所有时间戳发送完毕");

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
    public void testTs() {  // worker不应答
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

        ArrayList<TimeSeries> tsList = InsertAction.makeTsList(Parameters.Insert.batchTrans);
        while(tsList.size() > 0) {
            InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS, tsList);
            System.out.println("发送时间戳" + CacheUtil.timeSeriesLinkedList.size() + " " + tsList.size());
            channelFuture.channel().writeAndFlush(instructTs);
            if (tsList.size() != 1000) {
                System.out.println(tsList.get(tsList.size() - 1));
            }
            tsList = InsertAction.makeTsList(Parameters.Insert.batchTrans);
        }
        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.INSERT_TS_FINISH, null);
        channelFuture.channel().writeAndFlush(instructTs);
        System.out.println("所有时间戳发送完毕");

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
    public void testTs3() {
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