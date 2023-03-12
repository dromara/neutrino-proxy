package fun.asgc.neutrino.proxy.server.proxy.handler;

import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
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
