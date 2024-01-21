package org.dromara.neutrinoproxy.client.sdk.handler;

import com.google.common.collect.Lists;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import org.dromara.neutrinoproxy.client.sdk.config.IProxyConfiguration;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.sdk.core.IAbProxyClientService;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.aot.NeutrinoCoreRuntimeNativeRegistrar;
import org.dromara.neutrinoproxy.core.dispatcher.DefaultDispatcher;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;

import java.util.List;

/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
public abstract class ProxyMessageFactory extends IProxyConfiguration {

    public abstract void beanInject(String beanName, Object t);
    public abstract Object getBean(String beanName,Class c);

    public abstract void stop();
    public abstract boolean isAotRuntime();

    public void start(ProxyConfig proxyConfig){
        init(proxyConfig);
        IAbProxyClientService  clientService=new IAbProxyClientService();
        clientService.setProxyConfig(proxyConfig);
        clientService.setCmdTunnelBootstrap((Bootstrap) getBean("cmdTunnelBootstrap",Bootstrap.class));
        clientService.setUdpServerBootstrap((Bootstrap) getBean("udpServerBootstrap",Bootstrap.class));
        clientService.setIsAotRuntime(isAotRuntime());
        clientService.init();
    }

    public void init(ProxyConfig proxyConfig){
        NioEventLoopGroup tunnelWorkGroup = super.tunnelWorkGroup(proxyConfig);
        beanInject("tunnelWorkGroup",tunnelWorkGroup);
        Object tunnelWorkGroup1 = getBean("tunnelWorkGroup",NioEventLoopGroup.class);
        NioEventLoopGroup tcpRealServerWorkGroup = super.tcpRealServerWorkGroup(proxyConfig);
        beanInject("tcpRealServerWorkGroup",tcpRealServerWorkGroup);
        NioEventLoopGroup udpServerGroup = super.udpServerGroup(proxyConfig);
        beanInject("udpServerGroup",udpServerGroup);
        NioEventLoopGroup udpWorkGroup = super.udpWorkGroup(proxyConfig);
        beanInject("udpWorkGroup",udpWorkGroup);
        Bootstrap cmdTunnelBootstrap = super.cmdTunnelBootstrap(proxyConfig, tunnelWorkGroup);
        beanInject("cmdTunnelBootstrap",cmdTunnelBootstrap);
        Bootstrap tcpProxyTunnelBootstrap = super.tcpProxyTunnelBootstrap(proxyConfig, tunnelWorkGroup);
        beanInject("tcpProxyTunnelBootstrap",tcpProxyTunnelBootstrap);
        Bootstrap udpProxyTunnelBootstrap = super.udpProxyTunnelBootstrap(proxyConfig, tunnelWorkGroup);
        beanInject("udpProxyTunnelBootstrap",udpProxyTunnelBootstrap);
        Bootstrap realServerBootstrap = super.realServerBootstrap(proxyConfig, tcpRealServerWorkGroup);
        beanInject("realServerBootstrap",realServerBootstrap);
        Bootstrap udpServerBootstrap = super.udpServerBootstrap(proxyConfig, udpServerGroup, udpWorkGroup);
        beanInject("udpServerBootstrap",udpServerBootstrap);
//        NeutrinoCoreRuntimeNativeRegistrar neutrinoCoreRuntimeNativeRegistrar = super.neutrinoCoreRuntimeNativeRegistrar();
//        beanInject("neutrinoCoreRuntimeNativeRegistrar",neutrinoCoreRuntimeNativeRegistrar);
        dispatcher(proxyConfig, tcpProxyTunnelBootstrap, realServerBootstrap);
    }
    public  void dispatcher(ProxyConfig proxyConfig, Bootstrap tcpProxyTunnelBootstrap, Bootstrap realServerBootstrap) {
        List<ProxyMessageHandler> list = Lists.newArrayList(
            new ProxyMessageAuthHandler(proxyConfig,()->stop()),
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
        beanInject("dispatcher",dispatcher);
    }
}
