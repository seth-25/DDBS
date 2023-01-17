package com.distributed.worker.insert;

import com.distributed.util.*;
import com.distributed.worker.insert_netty_client.InsertClient;
import common.domain.InstructTs;
import common.domain.MsgInsert;
import common.domain.Sax;
import common.domain.TimeSeries;
import com.distributed.util.FileUtil;
import common.setting.Parameters;
import common.util.InstructUtil;
import common.util.MsgUtil;
import common.util.SaxUtil;
import common.util.TsUtil;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import common.setting.Constants;
public class InsertAction {

//    public static void sendTs(TimeSeries timeSeries) throws InterruptedException {
//        System.out.println("worker发送ts");
//        int hashValue = TsUtil.computeHash(timeSeries);
//        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
//            int left = entry.getValue().getKey();
//            int right = entry.getValue().getValue();
//            if (left <= hashValue && hashValue <= right) {
//                InsertClient insertClient = CacheUtil.workerInsertClient.get(entry.getKey());
//                InstructTs instructTs = InstructUtil.buildInstructTs(Constants.MsgType.SEND_TS, timeSeries);
//                insertClient.getChannel().writeAndFlush(instructTs);
//            }
//        }
//    }


//    public static void tempStoreTs(byte[] tsByteList) throws InterruptedException { // 暂存ts
//
//        assert tsByteList.length % Parameters.tsSize == 0;
//        ArrayList<TimeSeries> tsList = new ArrayList<>();
//        for (int i = 0; i < tsByteList.length / Parameters.tsSize; i ++ ) {
//            byte[] tsData = new byte[Parameters.timeSeriesDataSize];
//            byte[] timeStamp = new byte[Parameters.timeStampSize];
//            System.arraycopy(tsByteList, i * Parameters.tsSize, tsData, 0, Parameters.timeSeriesDataSize);
//            System.arraycopy(tsByteList, i * Parameters.tsSize + Parameters.timeSeriesDataSize, timeStamp, 0, Parameters.timeStampSize);
//            tsList.add(new TimeSeries(tsData, timeStamp));
//        }
//
//        final int taskCount = CacheUtil.timeStampRanges.size();    // 任务总数
//        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String hostName = entry.getKey();
//                    int left = entry.getValue().getKey();
//                    int right = entry.getValue().getValue();
//                    ArrayList<TimeSeries> tmpTsList = CacheUtil.tempTsList.computeIfAbsent(hostName, value -> new ArrayList<>());
//                    for (TimeSeries ts: tsList) {
//                        int hashValue = TsUtil.computeHash(ts);
//                        if (left <= hashValue && hashValue <= right) {
//                            synchronized (tmpTsList) {
//                                tmpTsList.add(ts);
//                            }
//                        }
//                    }
//
//                    if (CacheUtil.tempTsListCnt.containsKey(hostName)) {
//                        CacheUtil.tempTsListCnt.put(hostName, CacheUtil.tempTsListCnt.get(hostName) + 1);
//                    }
//                    else {
//                        CacheUtil.tempTsListCnt.put(hostName, 1);
//                    }
//
//                    countDownLatch.countDown();
//                }
//            };
//            CacheUtil.newFixedThreadPool.execute(runnable);
//        }
//        countDownLatch.await(); //  等待所有线程结束
//    }

//    public static void checkStoreTs() throws InterruptedException { // 检查暂存的ts，达到一定数量则发送给对应worker
//        final int taskCount = CacheUtil.tempTsList.size();    // 任务总数
//        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        for (Map.Entry<String, ArrayList<TimeSeries>> entry: CacheUtil.tempTsList.entrySet()) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String hostName = entry.getKey();
//                    ArrayList<TimeSeries> tmpTsList = CacheUtil.tempTsList.get(hostName);
//                    if (tmpTsList != null && tmpTsList.size() > Parameters.Insert.batchTrans || CacheUtil.tempTsListCnt.get(hostName) >= Parameters.Insert.cntTrans) {
//                        // 向对应worker发送ts
//
//                        byte[] data = new byte[tmpTsList.size() * Parameters.tsSize];
//                        int cnt = 0;
//                        for (TimeSeries ts: tmpTsList) {
////                            System.arraycopy(ts.getTimeSeriesData(), 0, data, cnt * Parameters.tsSize, );
//                            cnt ++ ;
//                        }
////                        MsgTs msgTs = MsgUtil.buildMsgTs(Constants.MsgType.SEND_TS, )
//
//                        InsertClient insertClient = CacheUtil.workerInsertClient.get(entry.getKey());
////                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.MsgType.SEND_TS, tmpTsList);
////                        tsClient.getChannel().writeAndFlush(instructTs);
//                        System.out.println("发送ts");
//                        CacheUtil.tempTsList.put(hostName, new ArrayList<>());
//                        CacheUtil.tempTsListCnt.put(hostName, 0);
//                    }
//                    countDownLatch.countDown();
//                }
//            };
//            CacheUtil.newFixedThreadPool.execute(runnable);
//        }
////        countDownLatch.await(); //  等待所有线程结束
//    }

//    public static void finalCheckStoreTs() throws InterruptedException { // 检查暂存的ts，将剩余的ts全部发送
//        System.out.println("最后检查ts");
//        final int taskCount = CacheUtil.tempTsList.size();    // 任务总数
//        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        for (Map.Entry<String, ArrayList<TimeSeries>> entry: CacheUtil.tempTsList.entrySet()) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String hostName = entry.getKey();
//                    ArrayList<TimeSeries> tmpTsList = CacheUtil.tempTsList.get(hostName);
//                    if (tmpTsList != null && tmpTsList.size() > 0) {
//                        // 向对应worker发送ts
//                        InsertClient insertClient = CacheUtil.workerInsertClient.get(entry.getKey());
//                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.MsgType.SEND_TS_FINISH, tmpTsList);
//                        insertClient.getChannel().writeAndFlush(instructTs);
//
//                        CacheUtil.tempTsList.put(hostName, new ArrayList<>());
//                        CacheUtil.tempTsListCnt.put(hostName, 0);
//                    }
//                    countDownLatch.countDown();
//                }
//            };
//            CacheUtil.newFixedThreadPool.execute(runnable);
//        }
////        countDownLatch.await(); //  等待所有线程结束
//    }

//    public static Sax tsToSax(TimeSeries timeSeries) throws IOException {
//        System.out.println("worker收到ts,将ts写入文件,并转化成sax");
//        long offset = FileUtil.writeFile(Parameters.tsFolder, timeSeries);
//        byte[] saxData = new byte[Parameters.saxDataSize];
//        DBUtil.dataBase.saxt_from_ts(timeSeries.getTimeSeriesData(), saxData);
//        return new Sax(saxData, (byte) TsUtil.computeHash(timeSeries), SaxUtil.createPointerOffset(offset), timeSeries.getTimeStamp());
//    }

    public static void insert() {
        CacheUtil.insertThreadPool.execute(new Insert());
    }

    /**
     * 多个时间序列+时间戳组成的byte数组,转化成多个leaftimekey(8位sax+7位文件偏移+1位文件名+8位时间戳)的byte数组
     */
    public static byte[] getLeafTimeKeys(byte[] tsBytes, int fileNum, long offset) {
        return DBUtil.dataBase.leaftimekey_from_tskey(tsBytes, fileNum, offset, false);
    }
    /**
     * 根据范围表,将sax发向对应的worker
     */
    public static void sendSax(byte[] leafTimeKeys) {

        byte[] tmpSendBytes = new byte[leafTimeKeys.length];
        int cnt = 0;

        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
            String hostName = entry.getKey();
            Sax left = new Sax(entry.getValue().getKey());
            Sax right = new Sax(entry.getValue().getValue());
            cnt = 0;
            for (int i = 0; i < leafTimeKeys.length / Parameters.saxSize; i ++ ) {
                byte[] saxData = new byte[Parameters.saxDataSize];
                System.arraycopy(leafTimeKeys, i * Parameters.saxSize + Parameters.saxPointerSize, saxData, 0, Parameters.saxDataSize);
                Sax sax = new Sax(saxData);
                if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
                    System.arraycopy(leafTimeKeys, i * Parameters.saxSize, tmpSendBytes, cnt * Parameters.saxSize, Parameters.saxSize);
                    cnt ++;
                }
            }
            if (cnt > 0) {
                byte[] sendBytes = new byte[cnt * Parameters.saxSize];
                System.arraycopy(tmpSendBytes, 0, sendBytes, 0, sendBytes.length);
                // 向对应worker发送sax
                InsertClient insertClient = CacheUtil.workerInsertClient.get(hostName);
                MsgInsert msgInsert = MsgUtil.buildMsgInsert(Constants.MsgType.SEND_SAX, sendBytes);
                insertClient.getChannel().writeAndFlush(msgInsert);
            }
        }
    }

    /**
     * 向数据库插入多个leaftimekey(8位sax+7位文件偏移+1位文件名+8位时间戳)的byte数组
     */
    public static void putSaxesBytes(byte[] leafTimeKeysBytes) {
        DBUtil.dataBase.put(leafTimeKeysBytes);
//        System.out.println("sax存储完成");
    }

//    public static ArrayList<Sax> tsToSax(ArrayList<TimeSeries> tsList) throws IOException {
//        System.out.println("worker收到ts,将ts写入文件,并转化成sax");
//        ArrayList<Sax> saxes = new ArrayList<>();
//        for (TimeSeries ts: tsList) {
//            long offset = FileUtil.writeTs(Parameters.tsFolder, ts);
//            byte[] saxData = new byte[Parameters.saxDataSize];
//            DBUtil.dataBase.saxt_from_ts(ts.getTimeSeriesData(), saxData);
//            saxes.add(new Sax(saxData, (byte) TsUtil.computeHash(ts), SaxUtil.createPointerOffset(offset), ts.getTimeStamp()));
//        }
//        return saxes;
//    }

//    public static void tempStoreSax(ArrayList<Sax> saxList) throws InterruptedException { // 暂存sax
//        final int taskCount = CacheUtil.timeStampRanges.size();    // 任务总数
//        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String hostName = entry.getKey();
//                    Sax left = new Sax(entry.getValue().getKey());
//                    Sax right = new Sax(entry.getValue().getValue());
//                    ArrayList<Sax> tmpSaxList = CacheUtil.tempSaxList.get(hostName);
//                    if (tmpSaxList == null) {
//                        tmpSaxList = new ArrayList<>();
//                        CacheUtil.tempSaxList.put(hostName, tmpSaxList);
//                    }
//
//                    for (Sax sax: saxList) {
//                        if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
//                            synchronized (tmpSaxList) {
//                                tmpSaxList.add(sax);
//                            }
//                        }
//                    }
//                    if (CacheUtil.tempSaxListCnt.containsKey(hostName)) {
//                        CacheUtil.tempSaxListCnt.put(hostName, CacheUtil.tempSaxListCnt.get(hostName) + 1);
//                    }
//                    else {
//                        CacheUtil.tempSaxListCnt.put(hostName, 1);
//                    }
//
//                    countDownLatch.countDown();
//                }
//            };
//            CacheUtil.insertThreadPool.execute(runnable);
//        }
//        countDownLatch.await(); //  等待所有线程结束
//    }
//
//    public static void checkStoreSax()  { // 检查暂存的sax，达到一定数量则发送给对应worker
//        final int taskCount = CacheUtil.tempSaxList.size();    // 任务总数
//        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        for (Map.Entry<String, ArrayList<Sax>> entry: CacheUtil.tempSaxList.entrySet()) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String hostName = entry.getKey();
//                    ArrayList<Sax> tmpSaxList = CacheUtil.tempSaxList.get(hostName);
//                    if (tmpSaxList != null && tmpSaxList.size() > Parameters.Insert.batchTrans ||
//                            CacheUtil.tempSaxListCnt.get(hostName) >= Parameters.Insert.cntTrans) {
//                        // 向对应worker发送sax
//                        InsertClient insertClient = CacheUtil.workerInsertClient.get(entry.getKey());
//                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.MsgType.SEND_SAX, tmpSaxList);
//                        insertClient.getChannel().writeAndFlush(instructTs);
//
//                        CacheUtil.tempSaxList.put(hostName, new ArrayList<>());
//                        CacheUtil.tempSaxListCnt.put(hostName, 0);
//                    }
//                    countDownLatch.countDown();
//                }
//            };
//            CacheUtil.insertThreadPool.execute(runnable);
//        }
////        countDownLatch.await(); //  等待所有线程结束
//    }
//
//    public static void finalCheckStoreSax()  { // 检查暂存的sax，把剩余的sax全部发送
//        System.out.println("最后检查sax");
//        final int taskCount = CacheUtil.tempSaxList.size();    // 任务总数
//        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        for (Map.Entry<String, ArrayList<Sax>> entry: CacheUtil.tempSaxList.entrySet()) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String hostName = entry.getKey();
//                    ArrayList<Sax> tmpSaxList = CacheUtil.tempSaxList.get(hostName);
//                    if (tmpSaxList != null && tmpSaxList.size() > 0) {
//                        // 向对应worker发送sax
//                        InsertClient insertClient = CacheUtil.workerInsertClient.get(entry.getKey());
//                        InstructTs instructTs = InstructUtil.buildInstructTs(Constants.MsgType.SEND_SAX, tmpSaxList);
//                        insertClient.getChannel().writeAndFlush(instructTs);
//
//                        CacheUtil.tempSaxList.put(hostName, new ArrayList<>());
//                        CacheUtil.tempSaxListCnt.put(hostName, 0);
//                    }
//                    countDownLatch.countDown();
//                }
//            };
//            CacheUtil.insertThreadPool.execute(runnable);
//        }
////        countDownLatch.await(); //  等待所有线程结束
//    }
//
//    public static void sendSax(Sax sax) {
//        System.out.println("worker发送sax");
//        for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
//            Sax left = new Sax(entry.getValue().getKey());
//            Sax right = new Sax(entry.getValue().getValue());
//            if (sax.compareTo(left) >= 0 && sax.compareTo(right) <= 0) {
//                InsertClient insertClient = CacheUtil.workerInsertClient.get(entry.getKey());
//                InstructTs instructTs = InstructUtil.buildInstructTs(Constants.MsgType.SEND_SAX, sax);
//                insertClient.getChannel().writeAndFlush(instructTs);
//            }
//        }
//    }

//    public static void putSax(ArrayList<Sax> saxes) throws InterruptedException {
////        for (Sax sax: saxes) {
////            DBUtil.dataBase.put(sax.getLeafTimeKeys());
////        }
//
//
////        CacheUtil.saxes.add(sax);
////        System.out.println("收到" + CacheUtil.saxes.size() + "条sax");
//        System.out.println("sax存储完成");
//    }
}
