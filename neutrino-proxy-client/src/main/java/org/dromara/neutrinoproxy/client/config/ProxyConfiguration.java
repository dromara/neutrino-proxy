package org.dromara.neutrinoproxy.client.config;

import com.google.common.collect.Lists;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import org.dromara.neutrinoproxy.client.sdk.config.IBeanHandler;
import org.dromara.neutrinoproxy.client.sdk.config.IProxyConfiguration;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.sdk.handler.*;
import org.dromara.neutrinoproxy.client.sdk.solon.BeanHandler;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.aot.NeutrinoCoreRuntimeNativeRegistrar;
import org.dromara.neutrinoproxy.core.dispatcher.DefaultDispatcher;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;

import java.util.List;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Configuration
public class ProxyConfiguration extends IProxyConfiguration implements  LifecycleBean {
    @Inject
    private ProxyConfig proxyConfig;
    @Inject("tcpProxyTunnelBootstrap")
    private Bootstrap tcpProxyTunnelBootstrap;
    @Inject("realServerBootstrap")
    private Bootstrap realServerBootstrap;

    @Override
    public void start() throws Throwable {
        List<ProxyMessageHandler> list = Lists.newArrayList(
            new ProxyMessageAuthHandler(proxyConfig),
            new ProxyMessageConnectHandler(tcpProxyTunnelBootstrap,realServerBootstrap,proxyConfig),
            new ProxyMessageDisconnectHandler(),
            new ProxyMessageErrorHandler(),
            new ProxyMessageTransferHandler(),
            new UdpProxyMessageConnectHandler(proxyConfig,tcpProxyTunnelBootstrap),
            new UdpProxyMessageTransferHandler()
        );
        Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher = new DefaultDispatcher<>("MessageDispatcher", list,
                proxyMessage -> ProxyDataTypeEnum.of((int)proxyMessage.getType()) == null ?
                        null : ProxyDataTypeEnum.of((int)proxyMessage.getType()).getName());
        Solon.context().wrapAndPut(Dispatcher.class, dispatcher);
    }

    @Override
    public IBeanHandler getBeanHandler() {
        return new BeanHandler();
    }
    @Bean("tunnelWorkGroup")
    public NioEventLoopGroup tunnelWorkGroup(@Inject ProxyConfig proxyConfig) {
        return super.tunnelWorkGroup(proxyConfig);
    }

    @Bean("tcpRealServerWorkGroup")
    public NioEventLoopGroup tcpRealServerWorkGroup(@Inject ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return super.tcpRealServerWorkGroup(proxyConfig);
    }

    @Bean("udpServerGroup")
    public NioEventLoopGroup udpServerGroup(@Inject ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return super.udpServerGroup(proxyConfig);
    }

    @Bean("udpWorkGroup")
    public NioEventLoopGroup udpWorkGroup(@Inject ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return super.udpWorkGroup(proxyConfig);
    }

    @Bean("cmdTunnelBootstrap")
    public Bootstrap cmdTunnelBootstrap(@Inject ProxyConfig proxyConfig,
                                        @Inject("tunnelWorkGroup") NioEventLoopGroup tunnelWorkGroup) {
        return super.cmdTunnelBootstrap(proxyConfig,tunnelWorkGroup);
    }

    @Bean("tcpProxyTunnelBootstrap")
    public Bootstrap tcpProxyTunnelBootstrap(@Inject ProxyConfig proxyConfig,
                                             @Inject("tunnelWorkGroup") NioEventLoopGroup tunnelWorkGroup) {
        return super.tcpProxyTunnelBootstrap(proxyConfig,tunnelWorkGroup);
    }

    @Bean("udpProxyTunnelBootstrap")
    public Bootstrap udpProxyTunnelBootstrap(@Inject ProxyConfig proxyConfig,
                                             @Inject("tunnelWorkGroup") NioEventLoopGroup tunnelWorkGroup) {
        return super.udpProxyTunnelBootstrap(proxyConfig,tunnelWorkGroup);
    }

    @Bean("realServerBootstrap")
    public Bootstrap realServerBootstrap(@Inject ProxyConfig proxyConfig,
                                         @Inject("tcpRealServerWorkGroup") NioEventLoopGroup tcpRealServerWorkGroup
    ) {
        return super.realServerBootstrap(proxyConfig,tcpRealServerWorkGroup);
    }

    @Bean("udpServerBootstrap")
    public Bootstrap udpServerBootstrap(@Inject ProxyConfig proxyConfig,
                                        @Inject("udpServerGroup") NioEventLoopGroup udpServerGroup,
                                        @Inject("udpWorkGroup") NioEventLoopGroup udpWorkGroup) {
        return super.udpServerBootstrap(proxyConfig,udpServerGroup,udpWorkGroup);
    }

    @Bean
    public NeutrinoCoreRuntimeNativeRegistrar neutrinoCoreRuntimeNativeRegistrar() {
        return super.neutrinoCoreRuntimeNativeRegistrar();
    }
}
