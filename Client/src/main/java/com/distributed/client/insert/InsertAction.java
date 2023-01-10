package com.distributed.client.insert;

import common.domain.TimeSeries;
import com.distributed.util.CacheUtil;

import java.util.ArrayList;

public class InsertAction {
    public static ArrayList<TimeSeries> makeTsList(int num) {
        ArrayList<TimeSeries> tsList = new ArrayList<>();
        for (int i = 0; i < num; i ++ ) {
            TimeSeries timeSeries = CacheUtil.timeSeriesLinkedList.poll();
            if (timeSeries == null) break;
            tsList.add(timeSeries);
        }
        return tsList;
    }
}
