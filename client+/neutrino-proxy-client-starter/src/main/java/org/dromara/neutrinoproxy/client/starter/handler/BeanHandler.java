package org.dromara.neutrinoproxy.client.starter.handler;

import cn.hutool.core.bean.BeanUtil;
import org.dromara.neutrinoproxy.client.sdk.config.IBeanHandler;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.starter.config.SpringProxyConfig;
import org.dromara.neutrinoproxy.client.starter.util.ApplicationContextUtils;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;


/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */

public class BeanHandler implements IBeanHandler {


     @Override
     public Dispatcher getDispatcher(){
         return (Dispatcher) ApplicationContextUtils.getBean("dispatcher");
    }

    @Override
    public ProxyConfig getProxyConfig() {
        SpringProxyConfig springProxyConfig =ApplicationContextUtils.getBean(SpringProxyConfig.class);
        ProxyConfig proxyConfig = BeanUtil.toBean(springProxyConfig, ProxyConfig.class);
        return proxyConfig;
    }
}
