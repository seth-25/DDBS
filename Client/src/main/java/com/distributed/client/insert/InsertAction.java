package com.distributed.client.insert;

import common.domain.TimeSeries;
import com.distributed.util.CacheUtil;
import common.setting.Parameters;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

    public static ByteBuf makeTsListByteBuf(int num) {
//        ArrayList<TimeSeries> tsList = new ArrayList<>();
        ByteBuf tsList = Unpooled.buffer(num * Parameters.tsSize);
        for (int i = 0; i < num; i ++ ) {
            TimeSeries timeSeries = CacheUtil.timeSeriesLinkedList.poll();
            if (timeSeries == null) break;
            tsList.writeBytes(timeSeries.getTimeSeriesData());
            tsList.writeBytes(timeSeries.getTimeStamp());
//            tsList.add(timeSeries);

        }
        return tsList;
    }
}
