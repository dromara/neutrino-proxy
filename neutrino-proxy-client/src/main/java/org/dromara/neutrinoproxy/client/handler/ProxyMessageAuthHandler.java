package org.dromara.neutrinoproxy.client.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.config.ProxyConfig;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ExceptionEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 认证信息处理器
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.AUTH)
@Component
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	@Inject
	private ProxyConfig proxyConfig;
	@Override
	public void handle(ChannelHandlerContext context, ProxyMessage proxyMessage) {
		String info = proxyMessage.getInfo();
        ONode load = ONode.load(info);
        Integer code = load.get("code").getInt();
		log.info("Auth result:{}", info);
		if (ExceptionEnum.AUTH_FAILED.getCode().equals(code)) {
			// 客户端认证失败，直接停止服务
			log.info("client auth failed , client stop.");
			context.channel().close();
			if (!proxyConfig.getTunnel().getReconnection().getUnlimited()) {
				Solon.stop();
			}
		} else if (ExceptionEnum.CONNECT_FAILED.getCode().equals(code) ||
				ExceptionEnum.LICENSE_CANNOT_REPEAT_CONNECT.getCode().equals(code)
		){
			context.channel().close();
		}
	}
}
