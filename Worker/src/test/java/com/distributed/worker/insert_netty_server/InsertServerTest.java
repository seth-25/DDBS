package com.distributed.worker.insert_netty_server;

import common.setting.Constants;
import common.setting.Parameters;
import com.distributed.util.CacheUtil;
import com.distributed.worker.instruct_netty_server.InstructServer;
import com.distributed.worker.insert_netty_client.InsertClient;
import javafx.util.Pair;

import java.util.Map;

public class InsertServerTest {


    public static void main(String[] args) throws InterruptedException {

//        DBUtil.dataBase.open("./db_data");
//        DBUtil.dataBase.init(new byte[Parameters.Init.numTs * Parameters.saxSize], Parameters.Init.numTs);

        CacheUtil.workerState = Constants.WorkerStatus.RUNNING;
//        CacheUtil.timeStampRanges.put("Ubuntu001", new Pair<>(0, 127));
//        CacheUtil.timeStampRanges.put("Ubuntu002", new Pair<>(128, 255));
        CacheUtil.timeStampRanges.put("Ubuntu002", new Pair<>(0, 255));
        byte[] left = {0, 0, 0, 0, 0, 0, 0, 0};
        byte[] right = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        CacheUtil.workerSaxRanges.put("Ubuntu002", new Pair<>(left, right));



        InsertServer insertServer = new InsertServer(Parameters.TsNettyServer.port);
        insertServer.start();
        InstructServer instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();

        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            InsertClient insertClient = new InsertClient(entry.getKey(), Parameters.TsNettyServer.port);
            insertClient.start();
            CacheUtil.workerTsClient.put(entry.getKey(), insertClient);
        }

        Thread.sleep(Long.MAX_VALUE);

        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            InsertClient insertClient = CacheUtil.workerTsClient.get(entry.getKey());
            insertClient.close();    // 关闭InstructTs连接
        }

//
//        byte[] a = new byte[1024];
//        long timeStamp = new Date().getTime() / 1000;
//        byte[] b = TsUtil.longToBytes(timeStamp);
//        System.out.println("生成时间戳:" + timeStamp + "  hash " + timeStamp % Parameters.tsHash);
//        TimeSeries timeSeries = new TimeSeries(a, b);
//        ChannelFuture channelFuture = CacheUtil.InsertWorkerChannel.get("Ubuntu002");
//        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_TS, timeSeries);
//        channelFuture.channel().writeAndFlush(instructTs);
    }
}
