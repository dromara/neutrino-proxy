package org.dromara.neutrinoproxy.client.starter.core;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.starter.config.ProxyConfiguration;
import org.dromara.neutrinoproxy.client.starter.config.SpringProxyConfig;
import org.dromara.neutrinoproxy.client.starter.ssh.SSHConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 代理客户端服务
 * @author: gc.x
 * @date: 2024/1/21
 */
@Slf4j
@Component
public class ProxyClientService implements ApplicationRunner {

    @Autowired
    private ProxyConfiguration proxyConfiguration;
    @Autowired
    private SpringProxyConfig springProxyConfig;
    @Override
    public void run(ApplicationArguments args) {
        if(springProxyConfig.getEnable()){
            log.info("start....");
            ProxyConfig proxyConfig = BeanUtil.toBean(springProxyConfig, ProxyConfig.class);
            proxyConfiguration.start(proxyConfig);
            log.info("start success!");
        }
        if(springProxyConfig.getSshEnable()){
            springProxyConfig.getSshProxys().forEach(sshProxy -> {
                String sshId = SSHConnectionFactory.factory.addConnection(sshProxy);
                SSHConnectionFactory.factory.openTunnel(sshId);
            });
        }
    }
}
