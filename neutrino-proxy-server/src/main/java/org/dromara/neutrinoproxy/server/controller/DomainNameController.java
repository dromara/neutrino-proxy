package org.dromara.neutrinoproxy.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Mapping("/domain-name")
@Controller
public class DomainNameController {
    @Inject
    private ProxyConfig proxyConfig;

    @Get
    @Mapping("/bind-info")
    public String bindInfo () {
        return proxyConfig.getServer().getTcp().getDomainName();
    }

}
