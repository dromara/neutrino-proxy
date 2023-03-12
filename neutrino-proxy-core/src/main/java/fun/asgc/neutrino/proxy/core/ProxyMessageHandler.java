package fun.asgc.neutrino.proxy.core;

import fun.asgc.neutrino.proxy.core.dispatcher.Handler;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface ProxyMessageHandler extends Handler<ChannelHandlerContext, ProxyMessage> {

}
