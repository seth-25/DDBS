package common.codec;

import common.domain.MsgTs;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TsEncoder extends MessageToByteEncoder<MsgTs> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgTs in, ByteBuf out) {
        out.writeByte(in.getType());
        out.writeByte(in.getLength());
        out.writeBytes(in.getData());
    }
}
