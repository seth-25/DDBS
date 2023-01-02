package com.distributed.util;

import com.distributed.domain.FileInfo;
import com.distributed.domain.Sax;
import com.distributed.domain.TimeSeries;
import com.distributed.worker.ts_netty_client.TsClient;
import io.netty.channel.ChannelFuture;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {

    public static Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();
    public static TreeMap<String, Long> cntInitSaxes = new TreeMap<>(); // 初始化时统计各种值的sax的个数
    public static ArrayList<TimeSeries> initTs = new ArrayList<>();
    public static ArrayList<Sax> initSaxes = new ArrayList<>(); // 初始化时的sax
    public static TreeMap<String, Pair<byte[], byte[]>> workerSaxRanges = new TreeMap<>(); // 各worker的hostname和负责的Sax范围

    public static TreeMap<String, Pair<Integer, Integer>> timeStampRanges = new TreeMap<>();  // 各worker的hostname和负责的时间序列的时间戳范围

    public static Map<String, TsClient> InsertWorkerChannel = new HashMap<>();

    public static String workerState;   // 当前worker的状态



    public static ArrayList<Sax> saxes = new ArrayList<>();

}
