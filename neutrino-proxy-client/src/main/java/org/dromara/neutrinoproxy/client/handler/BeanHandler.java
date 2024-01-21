package org.dromara.neutrinoproxy.client.handler;

import org.dromara.neutrinoproxy.client.sdk.config.IBeanHandler;
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
       return Solon.context().getBean(ProxyConfig.class);
    }
}
