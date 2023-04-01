package org.dromara.neutrinoproxy.server.proxy.handler;

import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import io.netty.channel.ChannelHandlerContext;
import org.noear.solon.annotation.Component;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Match(type = Constants.ProxyDataTypeName.HEARTBEAT)
@Component
public class ProxyMessageHeartbeatHandler implements ProxyMessageHandler {

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		ctx.channel().writeAndFlush(ProxyMessage.buildHeartbeatMessage());
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.HEARTBEAT.getDesc();
	}

}
