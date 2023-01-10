package leveldb_sax;

public class db {

    static {
        System.loadLibrary("leveldbj");
    }
    //sax

    public native void saxt_from_ts(byte[] ts, byte[] saxt);

    public native void paa_saxt_from_ts(byte[] ts, byte[] saxt, float[] paa);

    //db
    public native void open(String dbname);
    public native void close();

    public native void init(byte[] leafTimeKeys, int leafKeysNum);

    public native void put(byte[] leafTimeKey);

    // aquery
    // ts 256*4, startTime 8, endTime 8, k 4, 空4位，paa 32, saxt 8 一共1088
    // st_number
    // 选择的sstable
    // 返回至多k个ares
    //ares
//    ts 256*4 time 8
//    float dist 4 最后4位为空
//    1040
    public native byte[] Get(byte[] aquery, boolean is_use_am, int am_version_id, int st_version_id, long[] st_number);

    public native void unref_am(int version_id);

    public  native void unref_st(int version_id);






    public static void main(String[] args) {
        byte[] a = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        byte[] ts = new byte[1024];
        byte[] s = new byte[0];
        System.out.println(s.length);
//        db d = new db();
//        d.open("./testdb");
//        byte[] saxt = new byte[8];
//        d.saxt_from_ts(ts, saxt);
//        for(int i=0;i<8;i++){
//            System.out.println(saxt[i]);
//        }
//        d.close();
    }
}
