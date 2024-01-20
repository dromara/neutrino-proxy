package org.dromara.neutrinoproxy.client.sdk.core;

import io.netty.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

/**
 * 代理客户端服务
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Component
public class ProxyClientService extends IAbProxyClientService{
    @Inject
    private ProxyConfig proxyConfig;
    @Inject("cmdTunnelBootstrap")
    private Bootstrap cmdTunnelBootstrap;
    @Inject("udpServerBootstrap")
    private Bootstrap udpServerBootstrap;
	@Init
	public void init() {
        super.proxyConfig=proxyConfig;
        super.cmdTunnelBootstrap=cmdTunnelBootstrap;
        super.udpServerBootstrap=udpServerBootstrap;
        init_i();
	}
}
