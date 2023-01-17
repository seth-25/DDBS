package common.domain;

import java.nio.charset.StandardCharsets;

/**
 * 8位sax
 * 7位文件偏移
 * 1位文件名
 * 8位时间戳
 */
public class Sax implements Comparable<Sax>{
    private byte[] data;
    private byte[] p_offset;
    private byte p_hash;
    private byte[] timeStamp;

    public Sax(byte[] saxData) {
        this.data = saxData;
    }

    public Sax(byte[] saxData, byte[] p_offset, byte p_hash, byte[] timeStamp) {
        this.data = saxData;
        this.p_offset = p_offset;
        this.p_hash = p_hash;
        this.timeStamp = timeStamp;
    }

    public byte[] getData(){
        return data;
    }

    public byte[] getTimeStamp() {
        return timeStamp;
    }

    public byte[] getLeafTimeKeys() {
        byte[] res = new byte[data.length + 1 + p_offset.length + timeStamp.length];
        System.arraycopy(p_offset, 0, res, 0, p_offset.length);
        res[7] = p_hash;
        System.arraycopy(data, 0, res, 1 + p_offset.length, data.length);
        System.arraycopy(timeStamp, 0, res, data.length + 1 + p_offset.length, timeStamp.length);
        return res;
    }

    public int getSaxLength() {
        return data.length;
    }

    @Override
    public int compareTo(Sax o) {
        assert this.getSaxLength() == o.getSaxLength();
        byte[] a = this.getData();
        byte[] b = o.getData();
        for (int i = a.length - 1; i > 0; i -- ) {
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
