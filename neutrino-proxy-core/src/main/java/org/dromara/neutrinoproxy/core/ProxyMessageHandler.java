package org.dromara.neutrinoproxy.core;

import org.dromara.neutrinoproxy.core.dispatcher.Handler;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface ProxyMessageHandler extends Handler<ChannelHandlerContext, ProxyMessage> {

}
