package org.dromara.neutrinoproxy.client.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ExceptionEnum;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Component;

/**
 * 异常信息处理器
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.ERROR)
@Component
public class ProxyMessageErrorHandler implements ProxyMessageHandler {

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		log.info("error: {}", proxyMessage.getInfo());
        ONode load = ONode.load(proxyMessage.getInfo());
        Integer code = load.get("code").getInt();
		if (ExceptionEnum.AUTH_FAILED.getCode().equals(code)) {
			System.exit(0);
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.DISCONNECT.getDesc();
	}
}
