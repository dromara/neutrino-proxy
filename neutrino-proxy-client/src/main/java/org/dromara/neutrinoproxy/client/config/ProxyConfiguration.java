package org.dromara.neutrinoproxy.client.config;

import org.dromara.neutrinoproxy.client.handler.BeanHandler;
import org.dromara.neutrinoproxy.client.sdk.config.IBeanHandler;
import org.dromara.neutrinoproxy.client.sdk.handler.ProxyMessageFactory;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Configuration
public class ProxyConfiguration extends ProxyMessageFactory {


    @Override
    public IBeanHandler getBeanHandler() {
        return new BeanHandler();
    }

    @Override
    public void beanInject(String beanName, Object bean) {
        //包装Bean（指定名字的）
        BeanWrap beanWrap = Solon.context().wrap(beanName, bean);
        //以名字注册
        Solon.context().putWrap(beanName, beanWrap);
    }

    @Override
    public Object getBean(String beanName) {
        return Solon.context().getBean(beanName);
    }

    @Override
    public void stop() {
        Solon.stop();
    }

    @Override
    public boolean isAotRuntime() {
        return NativeDetector.isAotRuntime();
    }
}
