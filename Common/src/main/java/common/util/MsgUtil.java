package common.util;

import common.domain.MsgInsert;

public class MsgUtil {
    public static MsgInsert buildMsgInsert(int type, byte[] data) {
        return new MsgInsert(type, data.length, data);
    }



}
