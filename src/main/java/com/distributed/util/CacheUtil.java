package com.distributed.util;

import com.distributed.domain.FileInfo;
import com.distributed.domain.Sax;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    public static Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();

    public static TreeMap<String, Long> cntInitSaxes = new TreeMap<>(); // 初始化时统计各种值的sax的个数
    public static ArrayList<Sax> initSaxes = new ArrayList<>(); // 初始化时的sax
    public static TreeMap<String, Pair<byte[], byte[]>> workerRanges = new TreeMap(); // 各worker的hostname和负责的范围

}
