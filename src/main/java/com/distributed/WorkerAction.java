package com.distributed;

import com.distributed.domain.Constants;
import com.distributed.domain.InstructInit;
import com.distributed.domain.MyMessage;
import com.distributed.domain.Parameters;
import com.distributed.util.*;
import com.distributed.worker.file_netty_client.FileClient;
import com.distributed.worker.instruct_netty_client.InstructClient;
import io.netty.channel.ChannelFuture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerAction {

    public static void sendSaxStatics(String hostName) throws IOException, InterruptedException {
        InstructClient fileClient = new InstructClient(hostName, Parameters.InstructNettyClient.port);
        ChannelFuture channelFuture = fileClient.start();

        // sax统计很小，用instruct形式传输即可
        InstructInit instructInit = new InstructInit(Constants.InstructionType.SAX_STATISTIC);
        instructInit.setDataObject(CacheUtil.cntSaxes);
        channelFuture.channel().writeAndFlush(instructInit);
        try {
            channelFuture.channel().closeFuture().sync(); // 等待关闭
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        fileClient.close();

        System.out.println("sax统计传输完成");
        CacheUtil.cntSaxes = null;
        CoordinatorUtil.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.HAS_SENT_SAX_STATISTIC);
    }
    public static void sendSax(String hostName) throws IOException, InterruptedException {

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
