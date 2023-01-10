package common.domain;

import java.nio.charset.StandardCharsets;

public class Sax implements Comparable<Sax>{
    private byte[] data;
    private byte p_hash;
    private byte[] p_offset;
    private byte[] timeStamp;

    public Sax(byte[] saxData) {
        this.data = saxData;
    }

    public Sax(byte[] saxData, byte p_hash, byte[] p_offset, byte[] timeStamp) {
        this.data = saxData;
        this.p_hash = p_hash;
        this.p_offset = p_offset;
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
        System.arraycopy(data, 0, res, 0, data.length);
        res[data.length] = p_hash;
        System.arraycopy(p_offset, 0, res, data.length + 1, p_offset.length);
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
