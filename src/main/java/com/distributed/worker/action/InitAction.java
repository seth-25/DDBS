package com.distributed.worker.action;

import com.distributed.domain.*;
import com.distributed.util.*;
import com.distributed.worker.file_netty_client.FileClient;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitAction {

    //Worker向Master发送sax的统计
    public static void sendSaxStatics(String hostName) {
        InstructClient fileClient = new InstructClient(hostName, Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = fileClient.start();

        // sax统计很小，用instruct形式传输即可
        InstructInit instructInit = InstructUtil.buildInstruction(Constants.InstructionType.SAX_STATISTIC, CacheUtil.cntInitSaxes);
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

        TreeMap<String, ArrayList<Sax>> workerSaxes = new TreeMap<>();
        System.out.println("workerRanges"  + CacheUtil.workerRanges.get(Parameters.hostName));
        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerRanges.entrySet()) {
            ArrayList<Sax> saxes = new ArrayList<>();
            Sax left = new Sax(entry.getValue().getKey(), Parameters.dataSize);
            Sax right = new Sax(entry.getValue().getValue(), Parameters.dataSize);
            for (Sax sax: CacheUtil.initSaxes) {
                if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                    saxes.add(sax);
                }
            }
            workerSaxes.put(entry.getKey(), saxes);
        }
        CacheUtil.initSaxes = null;

        final int taskCount = workerSaxes.size();    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, ArrayList<Sax>> entry: workerSaxes.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    InstructClient instructClient = new InstructClient(entry.getKey(), Parameters.InstructNettyClient.port);
                    ChannelFuture channelFuture = instructClient.start();

                    InstructInit instructInit = InstructUtil.buildInstruction(Constants.InstructionType.SEND_SAX, entry.getValue());
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
    }

    public static void putSax(ArrayList<Sax> saxes) {
        for (Sax sax: saxes) {
            // levelDB, put接口
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
        CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.HAS_SENT_SAX_STATISTIC);
    }
}
