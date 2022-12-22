package com.distributed.util;

import com.distributed.domain.FileInfo;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    public static Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();
    public static TreeMap<String, Long> cntSaxes = new TreeMap<>(); // 统计各种值的sax的个数

}
