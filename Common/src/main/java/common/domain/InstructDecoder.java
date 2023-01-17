package common.domain;

import common.domain.protocol.PacketClazzMap;
import common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class InstructDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength + 5) {
            in.resetReaderIndex();
            return;
        }
        byte command = in.readByte();  //读取指令种类
        int type = in.readInt();  // 读取指令类型
        byte[] data = new byte[dataLength]; //指令占了一位，剔除掉
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(data, PacketClazzMap.packetTypeMap.get(command)));
    }
}

