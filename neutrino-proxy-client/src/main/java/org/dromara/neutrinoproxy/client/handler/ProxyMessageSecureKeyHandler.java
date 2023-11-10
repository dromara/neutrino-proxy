package org.dromara.neutrinoproxy.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.core.util.EncryptUtil;
import org.noear.solon.annotation.Component;

@Slf4j
@Match(type = Constants.ProxyDataTypeName.SECURE_KEY)
@Component
public class ProxyMessageSecureKeyHandler implements ProxyMessageHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        log.info("收到服务端的加密确认");
        Attribute<byte[]> secureKeyAttr = ctx.attr(Constants.SECURE_KEY);
        byte[] secureKey = secureKeyAttr.get();
        byte[] data = proxyMessage.getData();
        byte[] decryptedData = EncryptUtil.decryptByAes(secureKey, data);
        String m = new String(decryptedData);
        if ("ok".equals(m)) {
            // 设置当前链路为安全，之后使用该链路传输的消息均会加密
            Attribute<Boolean> booleanAttribute = ctx.attr(Constants.IS_SECURITY);
            booleanAttribute.set(true);

            ProxyUtil.setSecureKey(secureKey);

            log.info("Encrypted link established successfully");
        } else {
            ctx.channel().close();
        }
    }
}
