package com.distributed.master.init;

import common.setting.Constants;
import common.domain.InstructInit;
import com.distributed.domain.Parameters;
import com.distributed.master.instruct_netty_client.InstructClient;
import com.distributed.util.CacheUtil;
import common.util.InstructUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitAction {

    private static String getWorkerHostName(ChildData childData) {
        return childData.getPath().substring(Parameters.Zookeeper.workerFolder.length());
    }

    public static void addWorker(ChildData childData) {
        String workerHostName = getWorkerHostName(childData);
        CacheUtil.workerSaxRanges.put(workerHostName, null);
        CacheUtil.timeStampRanges.put(workerHostName, null);

        // 建立连接
        InstructClient instructClient = new InstructClient(workerHostName, Parameters.InstructNettyServer.port);
        instructClient.start();
        CacheUtil.workerInstructClient.put(workerHostName, instructClient);
    }

    private static void sendInstructs(List<ChildData> childDataList, String instruct, Object obj) {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);
        for (ChildData childData : childDataList) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
//                    InstructClient instructionClient = new InstructClient(getWorkerHostName(childData), Parameters.InstructNettyServer.port);
//                    ChannelFuture channelFuture = instructionClient.start();
                    Channel channel = CacheUtil.workerInstructClient.get(getWorkerHostName(childData)).getChannel();
                    InstructInit instructInit = InstructUtil.buildInstructInit(instruct, obj);
                    //发送信息
                    System.out.println("给" + getWorkerHostName(childData) +"发送指令 " + instruct);
                    channel.writeAndFlush(instructInit);
//                    try {
//                        channel.closeFuture().sync(); // 等待关闭
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    instructionClient.close();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
    }



    // 检查所有worker是否已经排序
    public static void checkSort(List<ChildData> childDataList) {
        boolean flag = true;
        for(ChildData childData : childDataList){
            String childStatus = new String(childData.getData());
            if (!childStatus.equals(Constants.WorkerStatus.HAS_SORT)) { // 有worker还没排完序
                flag = false;
            }
        }
        if (flag && childDataList.size() >= Parameters.numWorkerInit) { // 所有worker均启动，且排完序
            sendInstructs(childDataList, Constants.InstructionType.SEND_SAX_STATISTIC, Parameters.hostName);
        }
    }

    // 检查所有worker是否已经发送sax统计
    public static void checkSendSaxStatistic(List<ChildData> childDataList) throws IOException {
        boolean flag = true;
        for(ChildData cd : childDataList){
            String childStatus = new String(cd.getData());
            System.out.println("hostname" + cd.getPath() + " " + childStatus);
            if (!childStatus.equals(Constants.WorkerStatus.HAS_SENT_SAX_STATISTIC)) { // 有worker还没发送sax的统计
                flag = false;
            }
        }
        if (flag) {
            // 归并排序
//            MergeSort mergeSort = new MergeSort();
//            BigInteger totalSax = mergeSort.mergeSort();


            // worker的sax统计很小，只需要内存排序
            MemorySort memorySort = new MemorySort();
            TreeMap<String, Long> sortSaxes = memorySort.sort();
            System.out.println("排序完成");


            // 划分范围
            DivideRange divideRange = new DivideRange();
            ArrayList<Pair<byte[], byte[]>> rangePairs =  divideRange.divide(sortSaxes);
            System.out.println("划分范围完成");

            int index = 0;
            // 记录每个worker的sax范围
            for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
                entry.setValue(rangePairs.get(index ++));
            }
            // 发送sax范围
            sendInstructs(childDataList, Constants.InstructionType.SAX_RANGES, CacheUtil.workerSaxRanges);
            System.out.println("sax范围：");
            for (Map.Entry<String, Pair<byte[], byte[]>> entry: CacheUtil.workerSaxRanges.entrySet()) {
                System.out.println(entry.getKey() + "|" + Arrays.toString(entry.getValue().getKey()) + "|" + Arrays.toString(entry.getValue().getValue()));
            }

            // 计算每个worker的ts范围
            int numTs = Parameters.tsHash / Parameters.numWorkerInit;
            index = 0;
            for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
                entry.setValue(new Pair<>(index, index + numTs - 1));
                if (index + numTs > Parameters.tsHash) {    // 不整除的放到最后一个范围里
                    entry.setValue(new Pair<>(index, Parameters.tsHash - 1));
                }
                index += numTs;
            }
            System.out.println();
            // 发送ts范围
            sendInstructs(childDataList, Constants.InstructionType.TS_RANGES, CacheUtil.timeStampRanges);
            System.out.println("ts范围：");
            for (Map.Entry<String, Pair<Integer, Integer>> entry: CacheUtil.timeStampRanges.entrySet()) {
                System.out.println(entry.getKey() + "|" + entry.getValue().getKey() + "|" + entry.getValue().getValue());
            }
        }
    }
}
