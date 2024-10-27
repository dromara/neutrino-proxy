package org.dromara.neutrinoproxy.client.core;

import io.netty.channel.Channel;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface ProxyChannelBorrowListener {

    void success(Channel channel);

    void error(Throwable cause);

}
