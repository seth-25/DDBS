package common.codec;

import common.util.MsgUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class InsertDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out){
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength + 4) {  // 数据长度+type长度
            in.resetReaderIndex();
            return;
        }
        int type = in.readInt();
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(MsgUtil.buildMsgInsert(type, data));
    }
}
