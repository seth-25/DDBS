package com.distributed.worker.init;

import com.distributed.util.*;
import com.distributed.worker.file_netty_client.FileClient;
import com.distributed.worker.instruct_netty_client.InstructClient;
import common.domain.FileMessage;
import common.domain.InstructInit;
import common.domain.Sax;
import common.domain.TimeSeries;
import common.setting.Parameters;
import common.util.FileMsgUtil;
import com.distributed.util.FileUtil;
import common.util.InstructUtil;
import common.util.TsUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import common.setting.Constants;
public class InitAction {

    public static void setInstructClientToMaster(String hostName) {
        InstructClient instructClient = new InstructClient(hostName, Parameters.InstructNettyServer.port);
        instructClient.start();
        CacheUtil.masterInstructClient = instructClient;
    }

    //Worker向Master发送sax的统计
    public static void sendSaxStatics(String hostName) {
//        InstructClient fileClient = new InstructClient(hostName, Parameters.InstructNettyClient.port);
//        ChannelFuture channelFuture = fileClient.start();
        Channel channel = CacheUtil.masterInstructClient.getChannel();

        // sax统计很小，用instruct形式传输即可
        InstructInit instructInit = InstructUtil.buildInstructInit(Constants.InstructionType.SAX_STATISTIC, CacheUtil.cntInitSaxes);
        channel.writeAndFlush(instructInit);
    }

    public static void sendSaxStaticsFinish() {
        CacheUtil.cntInitSaxes = null;
        CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.HAS_SENT_SAX_STATISTIC);
    }
    // Worker向Worker发送Sax
    public static void sendSax() throws InterruptedException {
        final int taskCount = CacheUtil.workerSaxRanges.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        TreeMap<String, ArrayList<Sax>> workerSaxes = new TreeMap<>();
        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ArrayList<Sax> saxes = new ArrayList<>();
                    Sax left = new Sax(entry.getValue().getKey());
                    Sax right = new Sax(entry.getValue().getValue());
                    for (Sax sax: CacheUtil.initSaxes) {
                        if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                            saxes.add(sax);
                        }
                    }
                    InstructClient instructClient = new InstructClient(entry.getKey(), Parameters.InstructNettyClient.port);
                    ChannelFuture channelFuture = instructClient.start();

                    InstructInit instructInit = InstructUtil.buildInstructInit(Constants.InstructionType.SEND_SAX, saxes);
                    channelFuture.channel().writeAndFlush(instructInit);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    instructClient.close();
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
        System.out.println("发送sax完成");
        CacheUtil.initSaxes = null;
    }
    // Worker收到其他Worker发来的Sax,存入数据库中
    public static void putSax(ArrayList<Sax> saxes) throws InterruptedException {
        final int taskCount = saxes.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Sax sax: saxes) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    DBUtil.dataBase.put(sax.getLeafTimeKeys());
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束

        if (CacheUtil.workerState.equals(Constants.WorkerStatus.INIT)) {
            CacheUtil.workerState = Constants.WorkerStatus.HAS_PUT_SAX;
        }
        else if (CacheUtil.workerState.equals(Constants.WorkerStatus.HAS_PUT_TS)) { //  通知zookeeper
            CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.RUNNING);
            CacheUtil.workerState = Constants.WorkerStatus.RUNNING;
        }
        System.out.println("存储sax完成");
    }

    // Worker向Worker发送Ts
    public static void sendTs() throws InterruptedException {
        final int taskCount = CacheUtil.timeStampRanges.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ArrayList<TimeSeries> timeSeriesList = new ArrayList<>();
                    int left = entry.getValue().getKey();
                    int right = entry.getValue().getValue();
                    for (TimeSeries timeSeries: CacheUtil.initTs) {
                        int hash = TsUtil.computeHash(timeSeries);
                        if (left <= hash && hash <= right) {
                            timeSeriesList.add(timeSeries);
                        }
                    }
                    InstructClient instructClient = new InstructClient(entry.getKey(), Parameters.InstructNettyClient.port);
                    ChannelFuture channelFuture = instructClient.start();

                    InstructInit instructInit = InstructUtil.buildInstructInit(Constants.InstructionType.SEND_TS, timeSeriesList);
                    channelFuture.channel().writeAndFlush(instructInit);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    instructClient.close();
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
        System.out.println("发送ts完成");
        CacheUtil.initTs = null;
    }



    public static void putTs(ArrayList<TimeSeries> timeSeriesList) throws IOException {
        for (TimeSeries timeSeries : timeSeriesList) {
            long offset = FileUtil.writeTs(Parameters.tsFolder, timeSeries);
        }
        if (CacheUtil.workerState.equals(Constants.WorkerStatus.INIT)) {
            CacheUtil.workerState = Constants.WorkerStatus.HAS_PUT_TS;
        }
        else if (CacheUtil.workerState.equals(Constants.WorkerStatus.HAS_PUT_SAX)) { //  通知zookeeper
            CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.RUNNING);
            CacheUtil.workerState = Constants.WorkerStatus.RUNNING;
        }
        System.out.println("存储Ts完成");
    }

    public static void sendFile(String hostName) throws IOException, InterruptedException {

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        ArrayList<File> files = FileUtil.getAllFile(Parameters.MergeSort.countSaxPath);

        final int taskCount = files.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        System.out.println("开始传输");
        for (File file: files) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    FileClient fileClient = new FileClient(hostName, Parameters.FileNettyServer.port);
                    ChannelFuture channelFuture = fileClient.start();

                    FileMessage fileMessage = FileMsgUtil.buildFileRequest(file.getAbsolutePath(), file.getName(), Constants.FileType.SAX_STATISTIC, file.length());
                    System.out.println("传输文件: " + file.getAbsolutePath());

                    channelFuture.channel().writeAndFlush(fileMessage);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    fileClient.close();
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);

        }
        countDownLatch.await(); //  等待所有线程结束
        System.out.println("传输完成");
    }
}
