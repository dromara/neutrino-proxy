package org.dromara.neutrinoproxy.client.sdk.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ExceptionEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;

/**
 * 认证信息处理器
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.AUTH)
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	private ProxyConfig proxyConfig;

    private Runnable stop;

    public ProxyMessageAuthHandler(ProxyConfig proxyConfig,Runnable stop){
        this.proxyConfig=proxyConfig;
        this.stop=stop;
    }
	@Override
	public void handle(ChannelHandlerContext context, ProxyMessage proxyMessage) {
		String info = proxyMessage.getInfo();
        JSONObject load = JSONUtil.parseObj(info);
        Integer code = load.getInt("code");
		log.info("Auth result:{}", info);
		if (ExceptionEnum.AUTH_FAILED.getCode().equals(code)) {
			// 客户端认证失败，直接停止服务
			log.info("client auth failed , client stop.");
			context.channel().close();
			if (!proxyConfig.getTunnel().getReconnection().getUnlimited()) {
				stop.run();
			}
		} else if (ExceptionEnum.CONNECT_FAILED.getCode().equals(code) ||
				ExceptionEnum.LICENSE_CANNOT_REPEAT_CONNECT.getCode().equals(code)
		){
			context.channel().close();
		}
	}
}
