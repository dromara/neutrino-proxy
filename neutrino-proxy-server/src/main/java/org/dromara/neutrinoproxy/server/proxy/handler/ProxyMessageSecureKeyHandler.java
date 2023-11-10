package org.dromara.neutrinoproxy.server.proxy.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.core.util.EncryptUtil;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.annotation.Component;

import java.util.Map;

@Slf4j
@Match(type= Constants.ProxyDataTypeName.SECURE_KEY)
@Component
public class ProxyMessageSecureKeyHandler implements ProxyMessageHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {

        log.info("收到客户端的加密信息");

        // data为加密后的密码，info为加密密码的摘要
        byte[] data = proxyMessage.getData();
        String receivedDigest = proxyMessage.getInfo();

        String digest = EncryptUtil.digestBySm3(data);
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
        byte[] secureKey = EncryptUtil.decryptBySm2(privateKey, data);

        // 传输密码存储ctx中
        Attribute<byte[]> secureKeyAttr = ctx.attr(Constants.SECURE_KEY);
        secureKeyAttr.setIfAbsent(secureKey);

        // 使用密码加密success给客户端表示密码已确认
        byte[] encryptedSuccessInfoData = EncryptUtil.encryptByAes(secureKey, "ok".getBytes());

        // 发送回去，以示确认
        ctx.writeAndFlush(ProxyMessage.buildSecureKeyReturnMessage(encryptedSuccessInfoData));
        ctx.flush();

        // 设置该链路以及相关链路状态为安全，之后使用链路传输的数据均会加密
        Integer licenseId = ctx.attr(Constants.LICENSE_ID).get();
        ProxyUtil.setSecureKey(licenseId, secureKey);
        ProxyUtil.setChannelSecurity(licenseId, ctx.channel());
        Map<String, Channel> channelMap = ProxyUtil.getVisitorChannels(ctx.channel());
        channelMap.values().forEach(channel -> ProxyUtil.setChannelSecurity(licenseId, channel));
        if (licenseId != null) {
            ProxyUtil.setLicenseIdRelativeChannelSecurity(licenseId);
        }
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.SECURE_KEY.getDesc();
    }
}
