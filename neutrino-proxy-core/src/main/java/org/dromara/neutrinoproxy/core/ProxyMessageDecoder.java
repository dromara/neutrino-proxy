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

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.util.EncryptUtil;

import static org.dromara.neutrinoproxy.core.Constants.*;

@Slf4j
/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class ProxyMessageDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     * @param lengthAdjustment
     * @param initialBytesToStrip
     */
    public ProxyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
            int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    /**
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     * @param lengthAdjustment
     * @param initialBytesToStrip
     * @param failFast
     */
    public ProxyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
            int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected ProxyMessage decode(ChannelHandlerContext ctx, ByteBuf in2) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, in2);
        if (in == null) {
            return null;
        }

        if (in.readableBytes() < HEADER_SIZE) {
            return null;
        }

        Attribute<Boolean> booleanAttribute = ctx.attr(Constants.IS_SECURITY);
        Boolean isSecurity = booleanAttribute.get();

        ByteBuf buf;

        // 考虑isSecurity为null的情况，null的情况也为false
        if (isSecurity != null && isSecurity) {
            int packageLength = in.readInt();
            if (in.readableBytes() < packageLength) {
                return null;
            }

            // 获取加密数据
            byte[] encryptedBytes = new byte[packageLength];
            in.readBytes(encryptedBytes);
            in.release();

            // 获取解密密钥
            Attribute<byte[]> secureKeyAttr = ctx.attr(SECURE_KEY);
            byte[] secureKey = secureKeyAttr.get();
            // 解密
            byte[] decryptedData = EncryptUtil.decryptByAes(secureKey, encryptedBytes);

            buf = Unpooled.wrappedBuffer(decryptedData);
        } else {
            buf = in;
        }

        ProxyMessage proxyMessage = new ProxyMessage();
        int frameLength = buf.readInt();
        byte type = buf.readByte();
        long sn = buf.readLong();

        proxyMessage.setSerialNumber(sn);

        proxyMessage.setType(type);

        int infoLength = buf.readInt();
        byte[] infoBytes = new byte[infoLength];
        buf.readBytes(infoBytes);
        proxyMessage.setInfo(new String(infoBytes));

        byte[] data = new byte[frameLength - TYPE_SIZE - SERIAL_NUMBER_SIZE - INFO_LENGTH_SIZE - infoLength];
        buf.readBytes(data);
        proxyMessage.setData(data);

        buf.release();

        if (isSecurity != null && isSecurity) {
            log.info("【ProxyMessage】-type:{},编码解密", proxyMessage.getType());
        }

        return proxyMessage;
    }
}
