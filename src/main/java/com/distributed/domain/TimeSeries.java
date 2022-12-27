package com.distributed.domain;

public class TimeSeries {
    private byte[] timeSeriesData;
    private byte[] timeStamp;   // 小端存储的long

    public TimeSeries(byte[] timeSeriesData, byte[] timeStamp) {
        this.timeSeriesData = timeSeriesData;
        this.timeStamp = timeStamp;
    }
    public byte[] getTimeSeriesData() {
        return timeSeriesData;
    }

    public byte[] getTimeStamp() {
        return timeStamp;
    }
}
