package org.dromara.neutrinoproxy.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.config.ProxyConfig;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ExceptionEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.core.util.EncryptUtil;
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
		log.info("Auth result: {}", load.get("msg").getString());
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

        // 是否进行通道加密
        if (!proxyConfig.getTunnel().getSm2EncryptEnable()) {
            return;
        }

        // 默认设置为非安全链路，需要服务端确认后，再设置为安全链路
        Attribute<Boolean> booleanAttribute = context.attr(Constants.IS_SECURITY);
        booleanAttribute.set(false);

        // 获取认证成功的后的公钥信息，并生成随机密码，加密发到服务端确认
        String publicKey = load.get("publicKey").getString();
        byte[] secureKey = EncryptUtil.generateAesKey();
        // 存储密码
        Attribute<byte[]> secureKeyAttr = context.attr(Constants.SECURE_KEY);
        secureKeyAttr.set(secureKey);

        // 使用SM2算法对密钥进行加密并发送到服务端
        byte[] encryptSecureKey = EncryptUtil.encryptBySm2(publicKey, secureKey);
        context.writeAndFlush(ProxyMessage.buildSecureKeyMessage(encryptSecureKey));
        context.flush();
	}
}
