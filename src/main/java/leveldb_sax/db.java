package leveldb_sax;

public class db {
    static {
        System.loadLibrary("leveldbj");
    }
    //sax
    public native void saxt_from_ts(byte[] ts, byte[] saxt);

    //db
    public native void open(String dbname);
    public native void close();
    public native void init(byte[] leafTimeKeys, int leafKeysNum);
    public native void put(byte[] leafTimeKey);


}
