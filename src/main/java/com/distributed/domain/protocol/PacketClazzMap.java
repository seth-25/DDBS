package com.distributed.domain.protocol;

import com.distributed.domain.InstructInit;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 虫洞栈：https://bugstack.cn
 * 公众号：bugstack虫洞栈  ｛关注获取学习源码｝
 * 虫洞群：①群5398358 ②群5360692
 * Create by fuzhengwei on 2019
 */
public class PacketClazzMap {

    public final static Map<Byte, Class<? extends Packet>> packetTypeMap = new ConcurrentHashMap<>();

    static {
        packetTypeMap.put(Command.init, InstructInit.class);
//        packetTypeMap.put(Command.Demo02, MsgDemo02.class);
//        packetTypeMap.put(Command.Demo03, MsgDemo03.class);
    }

}
