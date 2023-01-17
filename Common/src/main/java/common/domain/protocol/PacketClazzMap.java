package common.domain.protocol;

import common.domain.InstructInit;
import common.domain.InstructRun;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketClazzMap {

    public final static Map<Byte, Class<? extends Packet>> packetTypeMap = new ConcurrentHashMap<>();

    static {
        packetTypeMap.put(Command.init, InstructInit.class);
        packetTypeMap.put(Command.run, InstructRun.class);
//        packetTypeMap.put(Command.Demo03, MsgDemo03.class);
    }
}
