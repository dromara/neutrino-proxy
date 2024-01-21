package org.dromara.neutrinoproxy.client.core;

import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.config.ProxyConfiguration;
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
public class ProxyClientService{
    @Inject
    private ProxyConfiguration proxyConfiguration;
    @Inject
    private ProxyConfig proxyConfig;
	@Init
	public void init() {
        log.info("启动中....");
        proxyConfiguration.start(proxyConfig);
        log.info("启动成功....");
	}
}
