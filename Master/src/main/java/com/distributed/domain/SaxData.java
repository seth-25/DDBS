package com.distributed.domain;

import java.nio.charset.StandardCharsets;


public class SaxData implements Comparable<SaxData>{
    private byte[] data;
    private long cnt;
    public SaxData(byte[] sax, int dataSize, long cnt) {
        this.data = new byte[dataSize];
        this.cnt = cnt;
        System.arraycopy(sax, 0, this.data, 0, dataSize);
    }

    public SaxData(byte[] sax, int dataSize) {
        this.data = new byte[dataSize];
        System.arraycopy(sax, 0, this.data, 0, dataSize);
    }

    public byte[] getData(){
        return data;
    }

    public long getCnt() {
        return cnt;
    }

    public int getSaxLength() {
        return data.length;
    }

    @Override
    public int compareTo(SaxData o) {
        assert this.getSaxLength() == o.getSaxLength();
        byte[] a = this.getData();
        byte[] b = o.getData();
        for (int i = 0; i < a.length; i ++ ) {
            if ((a[i] & 0xff) < (b[i] & 0xff)) return -1;
            else if ((a[i] &0xff) > (b[i] & 0xff)) return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return new String(data, StandardCharsets.UTF_8);
    }
}

