package com.distributed.master.version;

import com.distributed.domain.*;
import com.distributed.master.instruct_netty_client.InstructClient;
import com.distributed.util.CacheUtil;
import common.util.InstructUtil;
import com.distributed.util.VersionUtil;
import common.domain.InstructRun;
import io.netty.channel.ChannelFuture;
import common.setting.Parameters;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import common.setting.Constants;
public class VersionAction {

    public static synchronized void unRefCurVersion() {
        CacheUtil.CurVersion.unRef();
        if (CacheUtil.CurVersion.getRef() == 0) {
            HashMap<String, Pair<Integer, Integer>> workerVersions = CacheUtil.CurVersion.getWorkerVersions();
            unRefWorkerVersions(workerVersions);
        }
    }

    private static synchronized void unRefWorkerVersions(HashMap<String, Pair<Integer, Integer>> workerVersions) {
        for (Map.Entry<String, Pair<Integer, Integer>> entry: workerVersions.entrySet()) {
            String hostName = entry.getKey();
            int inVer = entry.getValue().getKey();
            int outVer = entry.getValue().getValue();

            HashMap<Integer, Integer> workerInVersionMap = CacheUtil.workerInVerRef.get(hostName);
            workerInVersionMap.put(inVer, workerInVersionMap.get(inVer) - 1);

            workerInVersionMap = CacheUtil.workerOutVerRef.get(hostName);
            workerInVersionMap.put(outVer, workerInVersionMap.get(outVer) - 1);
        }
    }

    private static synchronized void refWorkerVersions(HashMap<String, Pair<Integer, Integer>> workerVersions) {
        for (Map.Entry<String, Pair<Integer, Integer>> entry: workerVersions.entrySet()) {
            String hostName = entry.getKey();
            int inVer = entry.getValue().getKey();
            int outVer = entry.getValue().getValue();

            HashMap<Integer, Integer> workerInVersionMap = CacheUtil.workerInVerRef.get(hostName);
            if (workerInVersionMap.containsKey(inVer)) {
                workerInVersionMap.put(inVer, workerInVersionMap.get(inVer) + 1);
            }
            else {
                workerInVersionMap.put(inVer, 1);
            }

            HashMap<Integer, Integer> workerOutVersionMap = CacheUtil.workerOutVerRef.get(hostName);
            if (workerOutVersionMap.containsKey(outVer)) {
                workerOutVersionMap.put(outVer, workerOutVersionMap.get(outVer) + 1);
            }
            else {
                workerOutVersionMap.put(outVer, 1);
            }
        }
    }

    public static synchronized void refCurVersion() {
        CacheUtil.CurVersion.addRef();
    }

    public static synchronized void updateCurVersion(Version version) {
        CacheUtil.CurVersion = version;
    }

    public static void checkWorkerVersion() {   // 检查worker的小版本的ref，如果=0则通知worker删除版本
        for (Map.Entry<String, HashMap<Integer, Integer>> entry: CacheUtil.workerInVerRef.entrySet()) {
            String hostName = entry.getKey();
            HashMap<Integer, Integer> workerInVersionMap = entry.getValue();
            Iterator<Map.Entry<Integer, Integer>> iterator = workerInVersionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Integer> inVer = iterator.next();
                int version = inVer.getKey();
                int ref = inVer.getValue();
                if (ref == 0) {
                    iterator.remove();
                    InstructClient instructionClient = new InstructClient(hostName, Parameters.InstructNettyServer.port);
                    ChannelFuture channelFuture = instructionClient.start();
                    InstructRun instructrun = InstructUtil.buildInstructRun(Constants.MsgType.DELETE_IN_VERSION, version);
                    //发送信息
                    System.out.println("给" + hostName +"发送指令 " + Constants.MsgType.DELETE_IN_VERSION);
                    channelFuture.channel().writeAndFlush(instructrun);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    instructionClient.close();
                }
            }
        }

        for (Map.Entry<String, HashMap<Integer, Integer>> entry: CacheUtil.workerOutVerRef.entrySet()) {
            String hostName = entry.getKey();
            HashMap<Integer, Integer> workerOutVersionMap = entry.getValue();
            Iterator<Map.Entry<Integer, Integer>> iterator = workerOutVersionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Integer> outVer = iterator.next();
                int version = outVer.getKey();
                int ref = outVer.getValue();
                if (ref == 0) {
                    iterator.remove();
                    InstructClient instructionClient = new InstructClient(hostName, Parameters.InstructNettyServer.port);
                    ChannelFuture channelFuture = instructionClient.start();
                    InstructRun instructrun = InstructUtil.buildInstructRun(Constants.MsgType.DELETE_OUT_VERSION, version);
                    //发送信息
                    System.out.println("给" + hostName +"发送指令 " + Constants.MsgType.DELETE_OUT_VERSION);
                    channelFuture.channel().writeAndFlush(instructrun);
                    try {
                        channelFuture.channel().closeFuture().sync(); // 等待关闭
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    instructionClient.close();
                }
            }
        }
    }

    public static void changeVersion(byte[] versionBytes, String workerHostName) {
        Version newVersion = CacheUtil.CurVersion.deepCopy();

        if (versionBytes[0] == 0) {
            Integer[] inVer = {0};
            Integer[] outVer = {0};
            Long[] fileNum = {0L};
            byte[] minSax = new byte[Parameters.saxDataSize];
            byte[] maxSax = new byte[Parameters.saxDataSize];
            Long[] minTime = {0L};
            Long[] maxTime = {0L};
            VersionUtil.analysisVersionBytes(versionBytes,
                    inVer, outVer, fileNum, minSax, maxSax, minTime, maxTime);

            newVersion.updateVersion(workerHostName, new Pair<>(inVer[0], outVer[0]));
            // todo rtree插入
            newVersion.addRef();    // 大版本ref+1
            refWorkerVersions(newVersion.getWorkerVersions());  // 该大版本包括所有worker的小版本ref+1
            unRefCurVersion();  // 上一个大版本ref-1
            updateCurVersion(newVersion);   // 新版本覆盖旧版本
        }
        else if (versionBytes[0] == 1) {
            Integer[] outVer = {0};
            ArrayList<Long> addFileNums = new ArrayList<>();
            ArrayList<byte[]> addMinSaxes = new ArrayList<>();
            ArrayList<byte[]> addMaxSaxes = new ArrayList<>();
            ArrayList<Long> addMinTimes = new ArrayList<>();
            ArrayList<Long> addMaxTimes = new ArrayList<>();
            ArrayList<byte[]> delMinSaxes = new ArrayList<>();
            ArrayList<byte[]> delMaxSaxes = new ArrayList<>();
            ArrayList<Long> delMinTimes = new ArrayList<>();
            ArrayList<Long> delMaxTimes = new ArrayList<>();
            VersionUtil.analysisVersionBytes(versionBytes, outVer,
                    addFileNums, addMinSaxes, addMaxSaxes, addMinTimes, addMaxTimes,
                    delMinSaxes, delMaxSaxes, delMinTimes, delMaxTimes);

            newVersion.updateVersion(workerHostName, outVer[0]);
            // todo rtree插入
            newVersion.addRef();
            refWorkerVersions(newVersion.getWorkerVersions());
            unRefCurVersion();
            updateCurVersion(newVersion);

        }
        else {
            throw new RuntimeException();
        }

    }

}



