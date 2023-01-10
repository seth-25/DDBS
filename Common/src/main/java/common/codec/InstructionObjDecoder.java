package common.codec;

import common.domain.protocol.PacketClazzMap;
import common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class InstructionObjDecoder  extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte command = in.readByte();  //读取指令
        byte[] data = new byte[dataLength - 1]; //指令占了一位，剔除掉
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(data, PacketClazzMap.packetTypeMap.get(command)));
    }
}
