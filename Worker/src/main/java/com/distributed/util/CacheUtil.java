package com.distributed.util;

import com.distributed.worker.instruct_netty_client.InstructClient;
import common.domain.FileInfo;
import common.setting.Parameters;
import common.domain.Sax;
import common.domain.TimeSeries;
import com.distributed.worker.insert_netty_client.InsertClient;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheUtil {

    public static Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();
    public static TreeMap<String, Long> cntInitSaxes = new TreeMap<>(); // 初始化时统计各种值的sax的个数
    public static ArrayList<TimeSeries> initTs = new ArrayList<>();
    public static ArrayList<Sax> initSaxes = new ArrayList<>(); // 初始化时的sax
    public static Map<String, Pair<byte[], byte[]>> workerSaxRanges = new ConcurrentHashMap<>(); // 各worker的hostname和负责的Sax范围

    public static Map<String, Pair<Integer, Integer>> timeStampRanges = new ConcurrentHashMap<>();  // 各worker的hostname和负责的时间序列的时间戳范围





    public static String workerState;   // 当前worker的状态

    public static Map<String, ArrayList<TimeSeries>> tempTsList = new ConcurrentHashMap<>();  // 暂存的ts
    public static Map<String, Integer> tempTsListCnt = new ConcurrentHashMap<>();  // 暂存的ts已经几轮传输没发送了
    public static Map<String, ArrayList<Sax>> tempSaxList = new ConcurrentHashMap<>();  // 暂存的sax
    public static Map<String, Integer> tempSaxListCnt = new ConcurrentHashMap<>();  // 暂存的ts

    public static ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Parameters.numThread);  // 线程池


    public static Map<String, InsertClient> workerTsClient = new ConcurrentHashMap<>();

    public static Map<String, InstructClient> workerInstructClient = new ConcurrentHashMap<>();
    public static InstructClient masterInstructClient;
}
