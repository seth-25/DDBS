package com.distributed.worker.sort;

import com.distributed.domain.Constants;
import com.distributed.domain.MyMessage;
import com.distributed.domain.Parameters;
import com.distributed.util.CoordinatorUtil;
import com.distributed.util.FileUtil;
import com.distributed.util.MsgUtil;
import com.distributed.worker.netty_client.MyNettyClient;
import io.netty.channel.ChannelFuture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerAction {


    public static void sendSaxStatics(String hostName) throws IOException, InterruptedException {


//        ArrayList<File> files = FileUtil.getAllFile(Parameters.MergeSort.countSaxPath);
//        for (File file: files) {
//            MyNettyClient myNettyClient = new MyNettyClient(hostName, Parameters.NettyClient.port);
//            ChannelFuture channelFuture = myNettyClient.start();
//
//            MyMessage myMessage = MsgUtil.buildFileRequest(file.getAbsolutePath(), file.getName(), file.length());
//            System.out.println(file.getAbsolutePath() + " " + file.getName() + " " + file.length());
//
//            channelFuture.channel().writeAndFlush(myMessage);
//            try {
//                channelFuture.channel().closeFuture().sync(); // 等待关闭
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            myNettyClient.close();

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        ArrayList<File> files = FileUtil.getAllFile(Parameters.MergeSort.countSaxPath);

        final int taskCount = files.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        System.out.println("开始传输");
        for (File file: files) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    MyNettyClient myNettyClient = new MyNettyClient(hostName, Parameters.NettyClient.port);
                    ChannelFuture channelFuture = myNettyClient.start();

                    MyMessage myMessage = MsgUtil.buildFileRequest(file.getAbsolutePath(), file.getName(), Constants.FileType.SAX_STATISTIC, file.length());
                    System.out.println("传输文件: " + file.getAbsolutePath());

                    channelFuture.channel().writeAndFlush(myMessage);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    myNettyClient.close();
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
