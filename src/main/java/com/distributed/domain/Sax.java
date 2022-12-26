package com.distributed.domain;

import java.nio.charset.StandardCharsets;

public class Sax implements Comparable<Sax>{
    private byte[] data;
    private byte[] p;
    private byte[] timeStamp;
    public Sax(byte[] sax, int dataSize, int pointerSize) {
        this.data = new byte[dataSize];
        this.p = new byte[pointerSize];
        System.arraycopy(sax, 0, this.data, 0, dataSize);
        System.arraycopy(sax, dataSize, this.p, 0, pointerSize);
    }

    public Sax(byte[] saxData) {
        this.data = saxData;
    }
    
    public Sax(byte[] saxData, byte[] p) {
        this.data = saxData;
        this.p = p;
    }

    public byte[] getData(){
        return data;
    }

    public byte[] getP() {
        return p;
    }

    public void setTimeStamp(byte[] timeStamp) {
        this.timeStamp = timeStamp;
    }

    public byte[] getTimeStamp() {
        return timeStamp;
    }

//    public byte[] getSax() {
//        byte[] sax = new byte[data.length + p.length];
//        System.arraycopy(data, 0, sax, 0, data.length);
//        System.arraycopy(p, 0, sax, data.length, p.length);
//        return sax;
//    }

    public int getSaxLength() {
        return data.length;
    }

    @Override
    public int compareTo(Sax o) {
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
