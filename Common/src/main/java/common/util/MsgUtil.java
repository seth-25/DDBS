package common.util;

import common.domain.MsgTs;

public class MsgUtil {
    public static MsgTs buildMsgTs(int type, byte[] data) {
        return new MsgTs(type, data.length, data);
    }

}
