package org.dromara.neutrinoproxy.client.handler;

import cn.hutool.core.bean.BeanUtil;
import org.dromara.neutrinoproxy.client.config.SolonProxyConfig;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;
import org.noear.solon.Solon;



public class BeanHandler implements IBeanHandler {


     @Override
     public Dispatcher getDispatcher(){
         return Solon.context().getBean("dispatcher");
    }

    @Override
    public ProxyConfig getProxyConfig() {
        SolonProxyConfig solonProxyConfig = Solon.context().getBean(SolonProxyConfig.class);
        ProxyConfig proxyConfig = BeanUtil.toBean(solonProxyConfig, ProxyConfig.class);
        return proxyConfig;
    }
}
