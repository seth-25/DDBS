package com.distributed.util;

import com.distributed.master.instruct_netty_client.InstructClient;
import common.domain.FileInfo;
import com.distributed.domain.Version;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    public static Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();

    public static ArrayList<TreeMap<String, Long>> cntWorkerSaxes = new ArrayList<>(); // 统计各种值的sax的个数
    public static HashMap<String, Pair<byte[], byte[]>> workerSaxRanges = new HashMap<>(); // 各worker的hostname和负责的范围
    public static HashMap<String, Pair<Integer, Integer>> timeStampRanges = new HashMap<>();  // 各worker的hostname和负责的时间序列的时间戳范围

    public static Version CurVersion;   // 当前的版本
    public static HashMap<String, HashMap<Integer, Integer>> workerInVerRef = new HashMap<>(); // 所有worker的内存版本ref
    public static HashMap<String, HashMap<Integer, Integer>> workerOutVerRef = new HashMap<>(); // 所有worker的外存版本ref

    public static Map<String, InstructClient> workerInstructClient = new ConcurrentHashMap<>();
}
