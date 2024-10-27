package org.dromara.neutrinoproxy.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import static org.dromara.neutrinoproxy.core.Constants.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class ProxyMessageEncoder extends MessageToByteEncoder<ProxyMessage> {

    public ProxyMessageEncoder() {

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProxyMessage msg, ByteBuf out) throws Exception {
        int bodyLength = TYPE_SIZE + SERIAL_NUMBER_SIZE + INFO_LENGTH_SIZE;
        byte[] infoBytes = null;
        if (msg.getInfo() != null) {
            infoBytes = msg.getInfo().getBytes();
            bodyLength += infoBytes.length;
        }

        if (msg.getData() != null) {
            bodyLength += msg.getData().length;
        }

        // write the total packet length but without length field's length.
        out.writeInt(bodyLength);

        out.writeByte(msg.getType());
        out.writeLong(msg.getSerialNumber());

        if (infoBytes != null) {
            out.writeInt(infoBytes.length);
            out.writeBytes(infoBytes);
        } else {
            out.writeInt(0x00);
        }

        if (msg.getData() != null) {
            out.writeBytes(msg.getData());
        }
    }
}
