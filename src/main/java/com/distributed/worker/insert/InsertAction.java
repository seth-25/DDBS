package com.distributed.worker.insert;

import com.distributed.domain.*;
import com.distributed.util.*;
import com.distributed.worker.instruct_netty_client.InstructClient;
import com.distributed.worker.ts_netty_client.TsClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertAction {

    public static void sendTs(TimeSeries timeSeries) throws InterruptedException {
        System.out.println("worker发送ts");
        int hashValue = TsUtil.computeHash(timeSeries);
        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            int left = entry.getValue().getKey();
            int right = entry.getValue().getValue();
            if (left <= hashValue && hashValue <= right) {
                TsClient tsClient = CacheUtil.InsertWorkerChannel.get(entry.getKey());
                InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_TS, timeSeries);
                tsClient.getChannel().writeAndFlush(instructTs);
            }
        }
    }

    public static Sax tsToSax(TimeSeries timeSeries) throws IOException {
        System.out.println("worker收到ts,将ts写入文件,并转化成sax");
        long offset = FileUtil.writeFile(Parameters.tsFolder, timeSeries);
        byte[] saxData = new byte[Parameters.saxDataSize];
        DBUtil.dataBase.saxt_from_ts(timeSeries.getTimeSeriesData(), saxData);
        return new Sax(saxData, (byte) TsUtil.computeHash(timeSeries), SaxUtil.createPointerOffset(offset), timeSeries.getTimeStamp());
    }

    public static void sendSax(Sax sax) {
        System.out.println("worker发送sax");
        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
            Sax left = new Sax(entry.getValue().getKey());
            Sax right = new Sax(entry.getValue().getValue());
            if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                TsClient tsClient = CacheUtil.InsertWorkerChannel.get(entry.getKey());
                InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_SAX, sax);
                tsClient.getChannel().writeAndFlush(instructTs);
            }
        }
    }

    public static void putSax(Sax sax) throws InterruptedException {
//        DBUtil.dataBase.put(sax.getLeafTimeKeys());

//        CacheUtil.saxes.add(sax);
//        System.out.println("收到" + CacheUtil.saxes.size() + "条sax");
        System.out.println("sax存储完成");
    }
}
