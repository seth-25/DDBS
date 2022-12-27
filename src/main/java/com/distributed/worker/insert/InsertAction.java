package com.distributed.worker.insert;

import com.distributed.domain.*;
import com.distributed.util.*;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertAction {

    private static void sendTs(ArrayList<TimeSeries> timeSeriesList) throws InterruptedException {
        System.out.println("worker发送ts");
        final int taskCount = CacheUtil.timeStampRanges.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int left = entry.getValue().getKey();
                    int right = entry.getValue().getValue();
                    ArrayList<TimeSeries> workerTimeSeries = new ArrayList<>();
                    for (TimeSeries timeSeries: timeSeriesList) {
                        int hashValue = TsUtil.computeHash(timeSeries);
                        if (left <= hashValue && hashValue <= right) {
                            workerTimeSeries.add(timeSeries);
                        }
                    }
                    if (workerTimeSeries.size() > 0) {
                        InstructClient instructClient = new InstructClient(entry.getKey(), Parameters.InstructNettyClient.port);
                        ChannelFuture channelFuture = instructClient.start();
                        InstructRun instructRun = InstructUtil.buildInstructRun(Constants.InstructionType.SEND_TS, workerTimeSeries);
                        channelFuture.channel().writeAndFlush(instructRun);
                        try {
                            channelFuture.channel().closeFuture().sync(); // 等待关闭
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        instructClient.close();
                    }
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
//        countDownLatch.await(); //  等待所有线程结束
    }

    public static void tempStoreTs(TimeSeries timeSeries) throws InterruptedException { // 暂存ts，达到一定数量则发送给对应机器
        CacheUtil.tempTsList.add(timeSeries);
        if (CacheUtil.tempTsList.size() >= Parameters.Insert.numTempTs && CacheUtil.workerState.equals(Constants.WorkerStatus.RUNNING)) {
            sendTs(CacheUtil.tempTsList);
            CacheUtil.tempTsList = new ArrayList<>();
        }
    }


    public static ArrayList<Sax> tsToSax(ArrayList<TimeSeries> timeSeriesList) throws InterruptedException, IOException {
        System.out.println("worker收到ts,转化成sax");
        ArrayList<Sax> saxes = new ArrayList<>();
        for (TimeSeries timeSeries : timeSeriesList) {
            long offset = FileUtil.writeFile(Parameters.tsFolder, timeSeries);
//             leveldb接口 tosax(timeSeries); todo
            byte[] saxData = DBUtil.db.saxDataFromTs(timeSeries.getTimeSeriesData());
            Sax sax = new Sax(saxData, (byte) TsUtil.computeHash(timeSeries), SaxUtil.createPointerOffset(offset), timeSeries.getTimeStamp());
            saxes.add(sax);
        }
        return saxes;
    }

    public static void sendSax(ArrayList<Sax> saxes) throws InterruptedException, IOException {
        System.out.println("worker发送sax");
        final int taskCount = CacheUtil.workerSaxRanges.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ArrayList<Sax> workerSaxes = new ArrayList<>();
                    Sax left = new Sax(entry.getValue().getKey());
                    Sax right = new Sax(entry.getValue().getValue());
                    for (Sax sax: saxes) {
                        if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                            workerSaxes.add(sax);
                        }
                    }
                    if (workerSaxes.size() > 0) {
                        InstructClient instructClient = new InstructClient(entry.getKey(), Parameters.InstructNettyClient.port);
                        ChannelFuture channelFuture = instructClient.start();

                        InstructRun instructRun = InstructUtil.buildInstructRun(Constants.InstructionType.SEND_SAX, workerSaxes);
                        channelFuture.channel().writeAndFlush(instructRun);
                        try {
                            channelFuture.channel().closeFuture().sync(); // 等待关闭
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        instructClient.close();
                    }
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
//        countDownLatch.await(); //  等待所有线程结束
    }

    public static void putSax(ArrayList<Sax> saxes) throws InterruptedException {
        final int taskCount = saxes.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Sax sax: saxes) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // leveldb 接口
                    DBUtil.db.put(sax.getLeafTimeKeys());
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
        System.out.println("sax存储完成");
    }
}
