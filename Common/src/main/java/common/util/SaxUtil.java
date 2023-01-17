package common.util;

import common.setting.Parameters;

public class SaxUtil {
    public static byte[] createPointerOffset(long offset) {
        byte[] p_offset = new byte[Parameters.saxPointerSize - 1];
        for (int i = 0; i < Parameters.saxPointerSize - 1; i ++ ) {
            p_offset[i] = (byte) (offset >> i * 8);  // 小端 从long的低位开始截断，放在地址低的地方
        }
        return p_offset;
    }

    public static long pointerOffsetToLong(byte[] p_offset) {
        long l = 0;
        for (int i = 0; i < p_offset.length; i ++ ) {
            l <<= 8;
            l |= (p_offset[p_offset.length - 1 - i] & 0xff);  // 小端
        }
        return l;
    }

//    int compare(byte[] a, byte[] b) {
//        assert a.length == b.length;
//        for (int i = a.length - 1; i > 0; i -- ) {
//            if ((a[i] & 0xff) < (b[i] & 0xff)) return -1;
//            else if ((a[i] &0xff) > (b[i] & 0xff)) return 1;
//        }
//        return 0;
//    }
}
