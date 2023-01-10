package com.distributed.worker.insert;

import com.distributed.util.*;
import com.distributed.worker.ts_netty_client.TsClient;
import common.domain.InstructTs;
import common.domain.Sax;
import common.domain.TimeSeries;
import com.distributed.util.FileUtil;
import common.setting.Parameters;
import common.util.InstructUtil;
import common.util.SaxUtil;
import common.util.TsUtil;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import common.setting.Constants;
public class InsertAction {

    public static void sendTs(TimeSeries timeSeries) throws InterruptedException {
        System.out.println("worker发送ts");
        int hashValue = TsUtil.computeHash(timeSeries);
        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            int left = entry.getValue().getKey();
            int right = entry.getValue().getValue();
            if (left <= hashValue && hashValue <= right) {
                TsClient tsClient = CacheUtil.workerTsClient.get(entry.getKey());
                InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_TS, timeSeries);
                tsClient.getChannel().writeAndFlush(instructTs);
            }
        }
    }

    public static void tempStoreTs(ArrayList<TimeSeries> tsList) throws InterruptedException { // 暂存ts
        final int taskCount = CacheUtil.timeStampRanges.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String hostName = entry.getKey();
                    int left = entry.getValue().getKey();
                    int right = entry.getValue().getValue();
                    ArrayList<TimeSeries> tmpTsList = CacheUtil.tempTsList.get(hostName);
                    if (tmpTsList == null) {
                        tmpTsList = new ArrayList<>();
                        CacheUtil.tempTsList.put(hostName, tmpTsList);
                    }
                    for (TimeSeries ts: tsList) {
                        int hashValue = TsUtil.computeHash(ts);
                        if (left <= hashValue && hashValue <= right) {
                            synchronized (tmpTsList) {
                                tmpTsList.add(ts);
                            }
                        }
                    }

                    if (CacheUtil.tempTsListCnt.containsKey(hostName)) {
                        CacheUtil.tempTsListCnt.put(hostName, CacheUtil.tempTsListCnt.get(hostName) + 1);
                    }
                    else {
                        CacheUtil.tempTsListCnt.put(hostName, 1);
                    }

                    countDownLatch.countDown();
                }
            };
            CacheUtil.newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
    }

    public static void checkStoreTs() throws InterruptedException { // 检查暂存的ts，达到一定数量则发送给对应worker
        final int taskCount = CacheUtil.tempTsList.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, ArrayList<TimeSeries>> entry: CacheUtil.tempTsList.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String hostName = entry.getKey();
                    ArrayList<TimeSeries> tmpTsList = CacheUtil.tempTsList.get(hostName);
                    if (tmpTsList != null && tmpTsList.size() > Parameters.Insert.batchTrans || CacheUtil.tempTsListCnt.get(hostName) >= Parameters.Insert.cntTrans) {
                        // 向对应worker发送ts
                        TsClient tsClient = CacheUtil.workerTsClient.get(entry.getKey());
                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_TS, tmpTsList);
                        tsClient.getChannel().writeAndFlush(instructTs);

                        CacheUtil.tempTsList.put(hostName, new ArrayList<>());
                        CacheUtil.tempTsListCnt.put(hostName, 0);
                    }
                    countDownLatch.countDown();
                }
            };
            CacheUtil.newFixedThreadPool.execute(runnable);
        }
//        countDownLatch.await(); //  等待所有线程结束
    }

    public static void finalCheckStoreTs() throws InterruptedException { // 检查暂存的ts，将剩余的ts全部发送
        System.out.println("最后检查ts");
        final int taskCount = CacheUtil.tempTsList.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, ArrayList<TimeSeries>> entry: CacheUtil.tempTsList.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String hostName = entry.getKey();
                    ArrayList<TimeSeries> tmpTsList = CacheUtil.tempTsList.get(hostName);
                    if (tmpTsList != null && tmpTsList.size() > 0) {
                        // 向对应worker发送ts
                        TsClient tsClient = CacheUtil.workerTsClient.get(entry.getKey());
                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_TS_FINISH, tmpTsList);
                        tsClient.getChannel().writeAndFlush(instructTs);

                        CacheUtil.tempTsList.put(hostName, new ArrayList<>());
                        CacheUtil.tempTsListCnt.put(hostName, 0);
                    }
                    countDownLatch.countDown();
                }
            };
            CacheUtil.newFixedThreadPool.execute(runnable);
        }
//        countDownLatch.await(); //  等待所有线程结束
    }

//    public static Sax tsToSax(TimeSeries timeSeries) throws IOException {
//        System.out.println("worker收到ts,将ts写入文件,并转化成sax");
//        long offset = FileUtil.writeFile(Parameters.tsFolder, timeSeries);
//        byte[] saxData = new byte[Parameters.saxDataSize];
//        DBUtil.dataBase.saxt_from_ts(timeSeries.getTimeSeriesData(), saxData);
//        return new Sax(saxData, (byte) TsUtil.computeHash(timeSeries), SaxUtil.createPointerOffset(offset), timeSeries.getTimeStamp());
//    }
    public static ArrayList<Sax> tsToSax(ArrayList<TimeSeries> tsList) throws IOException {
        System.out.println("worker收到ts,将ts写入文件,并转化成sax");
        ArrayList<Sax> saxes = new ArrayList<>();
        for (TimeSeries ts: tsList) {
            long offset = FileUtil.writeTs(Parameters.tsFolder, ts);
            byte[] saxData = new byte[Parameters.saxDataSize];
            DBUtil.dataBase.saxt_from_ts(ts.getTimeSeriesData(), saxData);
            saxes.add(new Sax(saxData, (byte) TsUtil.computeHash(ts), SaxUtil.createPointerOffset(offset), ts.getTimeStamp()));
        }
        return saxes;
    }

    public static void tempStoreSax(ArrayList<Sax> saxList) throws InterruptedException { // 暂存sax
        final int taskCount = CacheUtil.timeStampRanges.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String hostName = entry.getKey();
                    Sax left = new Sax(entry.getValue().getKey());
                    Sax right = new Sax(entry.getValue().getValue());
                    ArrayList<Sax> tmpSaxList = CacheUtil.tempSaxList.get(hostName);
                    if (tmpSaxList == null) {
                        tmpSaxList = new ArrayList<>();
                        CacheUtil.tempSaxList.put(hostName, tmpSaxList);
                    }

                    for (Sax sax: saxList) {
                        if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                            synchronized (tmpSaxList) {
                                tmpSaxList.add(sax);
                            }
                        }
                    }
                    if (CacheUtil.tempSaxListCnt.containsKey(hostName)) {
                        CacheUtil.tempSaxListCnt.put(hostName, CacheUtil.tempSaxListCnt.get(hostName) + 1);
                    }
                    else {
                        CacheUtil.tempSaxListCnt.put(hostName, 1);
                    }

                    countDownLatch.countDown();
                }
            };
            CacheUtil.newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
    }

    public static void checkStoreSax()  { // 检查暂存的sax，达到一定数量则发送给对应worker
        final int taskCount = CacheUtil.tempSaxList.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, ArrayList<Sax>> entry: CacheUtil.tempSaxList.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String hostName = entry.getKey();
                    ArrayList<Sax> tmpSaxList = CacheUtil.tempSaxList.get(hostName);
                    if (tmpSaxList != null && tmpSaxList.size() > Parameters.Insert.batchTrans ||
                            CacheUtil.tempSaxListCnt.get(hostName) >= Parameters.Insert.cntTrans) {
                        // 向对应worker发送sax
                        TsClient tsClient = CacheUtil.workerTsClient.get(entry.getKey());
                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_SAX, tmpSaxList);
                        tsClient.getChannel().writeAndFlush(instructTs);

                        CacheUtil.tempSaxList.put(hostName, new ArrayList<>());
                        CacheUtil.tempSaxListCnt.put(hostName, 0);
                    }
                    countDownLatch.countDown();
                }
            };
            CacheUtil.newFixedThreadPool.execute(runnable);
        }
//        countDownLatch.await(); //  等待所有线程结束
    }

    public static void finalCheckStoreSax()  { // 检查暂存的sax，把剩余的sax全部发送
        System.out.println("最后检查sax");
        final int taskCount = CacheUtil.tempSaxList.size();    // 任务总数
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (Map.Entry<String, ArrayList<Sax>> entry: CacheUtil.tempSaxList.entrySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String hostName = entry.getKey();
                    ArrayList<Sax> tmpSaxList = CacheUtil.tempSaxList.get(hostName);
                    if (tmpSaxList != null && tmpSaxList.size() > 0) {
                        // 向对应worker发送sax
                        TsClient tsClient = CacheUtil.workerTsClient.get(entry.getKey());
                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_SAX, tmpSaxList);
                        tsClient.getChannel().writeAndFlush(instructTs);

                        CacheUtil.tempSaxList.put(hostName, new ArrayList<>());
                        CacheUtil.tempSaxListCnt.put(hostName, 0);
                    }
                    countDownLatch.countDown();
                }
            };
            CacheUtil.newFixedThreadPool.execute(runnable);
        }
//        countDownLatch.await(); //  等待所有线程结束
    }

    public static void sendSax(Sax sax) {
        System.out.println("worker发送sax");
        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
            Sax left = new Sax(entry.getValue().getKey());
            Sax right = new Sax(entry.getValue().getValue());
            if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                TsClient tsClient = CacheUtil.workerTsClient.get(entry.getKey());
                InstructTs instructTs = InstructUtil.buildInstructTs(Constants.InstructionType.SEND_SAX, sax);
                tsClient.getChannel().writeAndFlush(instructTs);
            }
        }
    }

    public static void putSax(ArrayList<Sax> saxes) throws InterruptedException {
//        for (Sax sax: saxes) {
//            DBUtil.dataBase.put(sax.getLeafTimeKeys());
//        }


//        CacheUtil.saxes.add(sax);
//        System.out.println("收到" + CacheUtil.saxes.size() + "条sax");
        System.out.println("sax存储完成");
    }
}
