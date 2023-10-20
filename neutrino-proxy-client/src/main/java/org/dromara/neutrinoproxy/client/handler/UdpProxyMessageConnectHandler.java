package org.dromara.neutrinoproxy.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.core.ProxyChannelBorrowListener;
import org.dromara.neutrinoproxy.client.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author: aoshiguchen
 * @date: 2023/9/19
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_CONNECT)
@Component
public class UdpProxyMessageConnectHandler implements ProxyMessageHandler {
    @Inject
    private ProxyConfig proxyConfig;
    @Inject("udpProxyTunnelBootstrap")
    private Bootstrap udpProxyTunnelBootstrap;

    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final Channel cmdChannel = ctx.channel();
        final ProxyMessage.UdpBaseInfo udpBaseInfo = ONode.deserialize(proxyMessage.getInfo(), ProxyMessage.UdpBaseInfo.class);
        log.info("[UDP connect]info:{}", proxyMessage.getInfo());

        // 获取连接
        ProxyUtil.borrowUdpProxyChanel(udpProxyTunnelBootstrap, new ProxyChannelBorrowListener() {

            @Override
            public void success(Channel channel) {
                channel.writeAndFlush(ProxyMessage.buildUdpConnectMessage(new ProxyMessage.UdpBaseInfo()
                        .setVisitorId(udpBaseInfo.getVisitorId())
                        .setServerPort(udpBaseInfo.getServerPort())
                        .setTargetIp(udpBaseInfo.getTargetIp())
                        .setTargetPort(udpBaseInfo.getTargetPort())
                ).setData(proxyConfig.getTunnel().getLicenseKey().getBytes()));
            }

            @Override
            public void error(Throwable cause) {
                cmdChannel.writeAndFlush(ProxyMessage.buildDisconnectMessage(udpBaseInfo.toJsonString()));
            }
        });


    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.UDP_CONNECT.getDesc();
    }
}
