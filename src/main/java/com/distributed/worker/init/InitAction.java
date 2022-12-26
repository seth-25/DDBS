package com.distributed.worker.init;

import com.distributed.domain.*;
import com.distributed.util.*;
import com.distributed.worker.file_netty_client.FileClient;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitAction {

    //Worker向Master发送sax的统计
    public static void sendSaxStatics(String hostName) {
        InstructClient fileClient = new InstructClient(hostName, Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = fileClient.start();

        // sax统计很小，用instruct形式传输即可
        InstructInit instructInit = InstructUtil.buildInstructInit(Constants.InstructionType.SAX_STATISTIC, CacheUtil.cntInitSaxes);
        channelFuture.channel().writeAndFlush(instructInit);
        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        fileClient.close();

        System.out.println("sax统计传输完成");
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


    public static void putSax(ArrayList<Sax> saxes) {
        for (Sax sax: saxes) {
            // levelDB, put接口
        }
        if (CacheUtil.workerState.equals(Constants.WorkerStatus.INIT)) {
            CacheUtil.workerState = Constants.WorkerStatus.HAS_PUT_SAX;
        }
        else if (CacheUtil.workerState.equals(Constants.WorkerStatus.HAS_PUT_TS)) { //  通知zookeeper
            CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.RUNNING);
            CacheUtil.workerState = Constants.WorkerStatus.RUNNING;
        }
    }

    public static void putTs() {


        if (CacheUtil.workerState.equals(Constants.WorkerStatus.INIT)) {
            CacheUtil.workerState = Constants.WorkerStatus.HAS_PUT_TS;
        }
        else if (CacheUtil.workerState.equals(Constants.WorkerStatus.HAS_PUT_SAX)) { //  通知zookeeper
            CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.RUNNING);
            CacheUtil.workerState = Constants.WorkerStatus.RUNNING;
        }
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
                    FileClient fileClient = new FileClient(hostName, Parameters.FileNettyClient.port);
                    ChannelFuture channelFuture = fileClient.start();

                    MyMessage myMessage = MsgUtil.buildFileRequest(file.getAbsolutePath(), file.getName(), Constants.FileType.SAX_STATISTIC, file.length());
                    System.out.println("传输文件: " + file.getAbsolutePath());

                    channelFuture.channel().writeAndFlush(myMessage);
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
