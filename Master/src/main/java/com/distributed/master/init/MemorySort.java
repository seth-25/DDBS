package com.distributed.master.init;

import com.distributed.util.CacheUtil;

import java.util.ArrayList;
import java.util.TreeMap;

public class MemorySort {

    public TreeMap<String, Long> sort() {
        TreeMap<String, Long> sortSaxes = new TreeMap<>();
        for (TreeMap<String, Long> cntSaxes: CacheUtil.cntWorkerSaxes) {
            cntSaxes.forEach(
                    (key, value) -> sortSaxes.merge(key, value, (a, b)-> a + b )
            );
        }
//        CacheUtil.cntWorkerSaxes = null;
        CacheUtil.cntWorkerSaxes = new ArrayList<>();
        return sortSaxes;
    }
}
