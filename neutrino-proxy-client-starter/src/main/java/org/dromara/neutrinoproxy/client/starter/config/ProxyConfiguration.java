package org.dromara.neutrinoproxy.client.starter.config;

import org.dromara.neutrinoproxy.client.sdk.config.IBeanHandler;
import org.dromara.neutrinoproxy.client.sdk.handler.ProxyMessageFactory;
import org.dromara.neutrinoproxy.client.starter.handler.BeanHandler;
import org.dromara.neutrinoproxy.client.starter.util.ApplicationContextUtils;
import org.springframework.stereotype.Component;


/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Component
public class ProxyConfiguration extends ProxyMessageFactory {


    @Override
    public IBeanHandler getBeanHandler() {
        return new BeanHandler();
    }

    @Override
    public void beanInject(String beanName, Object bean) {
        ApplicationContextUtils.addBean(beanName,bean);
    }

    @Override
    public Object getBean(String beanName,Class c) {
        return ApplicationContextUtils.getBean(beanName,c);
    }

    @Override
    public void stop() {
        ApplicationContextUtils.stop();
    }

    @Override
    public boolean isAotRuntime() {
        return false;
    }
}
