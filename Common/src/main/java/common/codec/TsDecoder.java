package common.codec;

import common.util.InstructUtil;
import common.util.MsgUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

public class TsDecoder extends LengthFieldBasedFrameDecoder {

    public TsDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        in = (ByteBuf) super.decode(ctx, in);
        int type = in.readInt();
        int length = in.readInt();
        if (in.readableBytes() != length) {
            throw new RuntimeException("传输消息长度错误");
        }
        byte[] data = new byte[length];
        in.readBytes(data);
        return MsgUtil.buildMsgTs(type, data);
    }
}
