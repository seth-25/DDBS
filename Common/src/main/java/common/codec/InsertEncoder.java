package common.codec;

import common.domain.MsgInsert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class InsertEncoder extends MessageToByteEncoder<MsgInsert> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgInsert in, ByteBuf out) {
        out.writeInt(in.getLength());
        out.writeInt(in.getType());
        out.writeBytes(in.getData());
    }
}
