package fun.asgc.neutrino.proxy.client.handler;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.proxy.core.*;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
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
		log.info("异常信息: {}", proxyMessage.getInfo());
		JSONObject data = JSONObject.parseObject(proxyMessage.getInfo());
		Integer code = data.getInteger("code");
		if (ExceptionEnum.AUTH_FAILED.getCode().equals(code)) {
			System.exit(0);
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.DISCONNECT.getDesc();
	}
}
