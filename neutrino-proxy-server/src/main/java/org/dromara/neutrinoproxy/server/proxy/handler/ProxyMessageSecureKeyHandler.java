package org.dromara.neutrinoproxy.server.proxy.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.core.util.SmEncryptUtil;
import org.noear.solon.annotation.Component;

@Slf4j
@Match(type= Constants.ProxyDataTypeName.SECURE_KEY)
@Component
public class ProxyMessageSecureKeyHandler implements ProxyMessageHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {

        // data为加密后的密码，info为加密密码的摘要
        byte[] data = proxyMessage.getData();
        String receivedDigest = proxyMessage.getInfo();

        String digest = SmEncryptUtil.digestBySm3(data);
        if (!digest.equals(receivedDigest)) {
            // 获取加密信息失败
            log.warn("密码协商失败");
            // TODO 应该断开连接
            return;
        }

        // 获取私钥
        Attribute<String> privateKeyAttr = ctx.attr(Constants.SECURE_PRIVATE_KEY);
        String privateKey = privateKeyAttr.get();
        if (StrUtil.isEmpty(privateKey)) {
            // 获取私钥失败
            log.warn("获取私钥失败");
            // TODO 应该断开连接
            return;
        }

        // 解密传输密码
        byte[] secureKey = SmEncryptUtil.decryptBySm2(privateKey, data);

        // 传输密码存储ctx中
        Attribute<byte[]> secureKeyAttr = ctx.attr(Constants.SECURE_KEY);
        secureKeyAttr.setIfAbsent(secureKey);

        // 使用密码加密success给客户端表示密码已确认
        byte[] encryptedSuccessInfoData = SmEncryptUtil.encryptBySm4(secureKey, "ok".getBytes());

        // 发送回去，以示确认
        ctx.writeAndFlush(ProxyMessage.buildSecureKeyReturnMessage(encryptedSuccessInfoData));
        ctx.flush();
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.SECURE_KEY.getDesc();
    }
}
