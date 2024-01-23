package org.dromara.neutrinoproxy.client.sdk.handler;

import com.google.common.collect.Lists;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.neutrinoproxy.client.sdk.config.IProxyConfiguration;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.sdk.core.IAbProxyClientService;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.DefaultDispatcher;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
public abstract class ProxyMessageFactory extends IProxyConfiguration {

    public abstract void stop();
    public abstract boolean isAotRuntime();
    public static Map<String,BeanInfo> beanManager=new ConcurrentHashMap<>();
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeanInfo<T>{
        private String beanName;
        private T bean;
        private Class<?> beanClass;
    }

    public void start(ProxyConfig proxyConfig){
        init(proxyConfig);
        IAbProxyClientService  clientService=new IAbProxyClientService();
        clientService.setProxyConfig(proxyConfig);
        clientService.setCmdTunnelBootstrap((Bootstrap)beanManager.get("cmdTunnelBootstrap").getBean());
        clientService.setUdpServerBootstrap((Bootstrap)beanManager.get("udpServerBootstrap").getBean());
        clientService.setIsAotRuntime(isAotRuntime());
        clientService.init();
    }

    public void init(ProxyConfig proxyConfig){
        beanManager.put("proxyConfig",BeanInfo.builder().bean(proxyConfig).beanName("proxyConfig").beanClass(ProxyConfig.class).build());
        NioEventLoopGroup tunnelWorkGroup = super.tunnelWorkGroup(proxyConfig);
        beanManager.put("tunnelWorkGroup",BeanInfo.builder().bean(tunnelWorkGroup).beanName("tunnelWorkGroup").beanClass(NioEventLoopGroup.class).build());
        NioEventLoopGroup tcpRealServerWorkGroup = super.tcpRealServerWorkGroup(proxyConfig);
        beanManager.put("tcpRealServerWorkGroup",BeanInfo.builder().bean(tcpRealServerWorkGroup).beanName("tcpRealServerWorkGroup").beanClass(NioEventLoopGroup.class).build());
        NioEventLoopGroup udpServerGroup = super.udpServerGroup(proxyConfig);
        beanManager.put("udpServerGroup",BeanInfo.builder().bean(udpServerGroup).beanName("udpServerGroup").beanClass(NioEventLoopGroup.class).build());
        NioEventLoopGroup udpWorkGroup = super.udpWorkGroup(proxyConfig);
        beanManager.put("udpWorkGroup",BeanInfo.builder().bean(udpWorkGroup).beanName("udpWorkGroup").beanClass(NioEventLoopGroup.class).build());
        Bootstrap cmdTunnelBootstrap = super.cmdTunnelBootstrap(proxyConfig, tunnelWorkGroup);
        beanManager.put("cmdTunnelBootstrap",BeanInfo.builder().bean(cmdTunnelBootstrap).beanName("cmdTunnelBootstrap").beanClass(Bootstrap.class).build());
        Bootstrap tcpProxyTunnelBootstrap = super.tcpProxyTunnelBootstrap(proxyConfig, tunnelWorkGroup);
        beanManager.put("tcpProxyTunnelBootstrap",BeanInfo.builder().bean(tcpProxyTunnelBootstrap).beanName("tcpProxyTunnelBootstrap").beanClass(Bootstrap.class).build());
        Bootstrap udpProxyTunnelBootstrap = super.udpProxyTunnelBootstrap(proxyConfig, tunnelWorkGroup);
        beanManager.put("udpProxyTunnelBootstrap",BeanInfo.builder().bean(udpProxyTunnelBootstrap).beanName("udpProxyTunnelBootstrap").beanClass(Bootstrap.class).build());
        Bootstrap realServerBootstrap = super.realServerBootstrap(proxyConfig, tcpRealServerWorkGroup);
        beanManager.put("realServerBootstrap",BeanInfo.builder().bean(realServerBootstrap).beanName("realServerBootstrap").beanClass(Bootstrap.class).build());
        Bootstrap udpServerBootstrap = super.udpServerBootstrap(proxyConfig, udpServerGroup, udpWorkGroup);
        beanManager.put("udpServerBootstrap",BeanInfo.builder().bean(udpServerBootstrap).beanName("udpServerBootstrap").beanClass(Bootstrap.class).build());
        dispatcher(proxyConfig, tcpProxyTunnelBootstrap, realServerBootstrap);
    }
    public  void dispatcher(ProxyConfig proxyConfig, Bootstrap tcpProxyTunnelBootstrap, Bootstrap realServerBootstrap) {
        List<ProxyMessageHandler> list = Lists.newArrayList(
            new ProxyMessageAuthHandler(proxyConfig, this::stop),
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
        beanManager.put("dispatcher",BeanInfo.builder().bean(dispatcher).beanName("dispatcher").beanClass(Dispatcher.class).build());
    }
}
