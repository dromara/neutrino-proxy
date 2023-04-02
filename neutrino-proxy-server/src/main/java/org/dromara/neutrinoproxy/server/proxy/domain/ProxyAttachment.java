package org.dromara.neutrinoproxy.server.proxy.domain;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.function.BiConsumer;

/**
 * 代理连接附件
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
public class ProxyAttachment {
    private Channel channel;
    private byte[] bytes;
    private BiConsumer<Channel, byte[]> executor;

    public ProxyAttachment(Channel channel, byte[] bytes, BiConsumer<Channel, byte[]> executor) {
        this.channel = channel;
        this.bytes = bytes;
        this.executor = executor;
    }

    public void execute() {
        if (null != executor) {
            this.executor.accept(channel, bytes);
        }
    }
}
