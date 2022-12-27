package com.distributed;

public class DB {
    static {
        System.loadLibrary("leveldbj");
    }
    //sax
    public native byte[] saxDataFromTs(byte[] ts);

    //db
    public native void open(String dbname);
    public native void close();
    public native void init(byte[] leafTimeKeys, int leafKeysNum);
    public native void put(byte[] leafTimeKey);


}
