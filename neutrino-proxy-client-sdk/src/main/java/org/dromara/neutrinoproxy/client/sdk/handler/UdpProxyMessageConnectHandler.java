package org.dromara.neutrinoproxy.client.sdk.handler;

import cn.hutool.json.JSONUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.sdk.core.ProxyChannelBorrowListener;
import org.dromara.neutrinoproxy.client.sdk.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;

/**
 * @author: aoshiguchen
 * @date: 2023/9/19
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_CONNECT)
public class UdpProxyMessageConnectHandler implements ProxyMessageHandler {

    private final ProxyConfig proxyConfig;
    private final Bootstrap udpProxyTunnelBootstrap;

    public UdpProxyMessageConnectHandler(ProxyConfig proxyConfig,Bootstrap udpProxyTunnelBootstrap){
        this.proxyConfig=proxyConfig;
        this.udpProxyTunnelBootstrap=udpProxyTunnelBootstrap;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final Channel cmdChannel = ctx.channel();
        String info = proxyMessage.getInfo();
        final ProxyMessage.UdpBaseInfo udpBaseInfo = JSONUtil.toBean(info,ProxyMessage.UdpBaseInfo.class);
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
