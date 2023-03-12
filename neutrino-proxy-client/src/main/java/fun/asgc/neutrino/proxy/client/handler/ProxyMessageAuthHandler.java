package fun.asgc.neutrino.proxy.client.handler;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

/**
 * 认证信息处理器
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.AUTH)
@Component
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	@Override
	public void handle(ChannelHandlerContext context, ProxyMessage proxyMessage) {
		String info = proxyMessage.getInfo();
		JSONObject data = JSONObject.parseObject(info);
		Integer code = data.getInteger("code");
		log.info("认证结果:{}", info);
	}
}
