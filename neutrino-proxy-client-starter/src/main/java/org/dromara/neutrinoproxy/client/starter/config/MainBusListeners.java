package org.dromara.neutrinoproxy.client.starter.config;

import org.dromara.neutrinoproxy.client.starter.util.ApplicationContextUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动监听
 * @author: gc.x
 * @date: 2024/1/21
 */
@Component
public class MainBusListeners implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContextUtils.setContext(event.getApplicationContext());
    }
}