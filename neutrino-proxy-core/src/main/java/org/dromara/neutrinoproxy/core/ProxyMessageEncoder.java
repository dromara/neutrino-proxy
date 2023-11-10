/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.neutrinoproxy.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.util.EncryptUtil;

import static org.dromara.neutrinoproxy.core.Constants.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
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

        Attribute<Boolean> booleanAttribute = ctx.attr(Constants.IS_SECURITY);
        Boolean isSecurity = booleanAttribute.get();

        ByteBuf buf;

        // 考虑isSecurity为null的情况，null的情况也为false
        if (isSecurity != null && isSecurity) {
            buf = Unpooled.directBuffer(bodyLength);
        } else {
            buf = out;
        }

        // write the total packet length but without length field's length.
        buf.writeInt(bodyLength);

        buf.writeByte(msg.getType());
        buf.writeLong(msg.getSerialNumber());

        if (infoBytes != null) {
            buf.writeInt(infoBytes.length);
            buf.writeBytes(infoBytes);
        } else {
            buf.writeInt(0x00);
        }

        if (msg.getData() != null) {
            buf.writeBytes(msg.getData());
        }

        // 考虑isSecurity为null的情况，null的情况也为false
        if (isSecurity != null && isSecurity) {

            log.info("【ProxyMessage】-type:{},编码加密", msg.getType());

            // 执行加密
            byte[] data = new byte[buf.writerIndex()];
            buf.readBytes(data);

            // 获取加密密钥
            Attribute<byte[]> secureKeyAttr = ctx.attr(SECURE_KEY);
            byte[] secureKey = secureKeyAttr.get();
            // 执行加密
            byte[] encryptedData = EncryptUtil.encryptByAes(secureKey, data);
            out.writeInt(encryptedData.length);
            out.writeBytes(encryptedData);
            buf.release();
        }

    }
}
