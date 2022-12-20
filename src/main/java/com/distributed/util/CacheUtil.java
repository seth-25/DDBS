package com.distributed.util;

import com.distributed.domain.FileInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    public static Map<String, FileInfo> fileInfoMap = new ConcurrentHashMap<>();
}
