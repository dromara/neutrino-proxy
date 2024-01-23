package org.dromara.neutrinoproxy.client.config;

import org.dromara.neutrinoproxy.client.sdk.handler.ProxyMessageFactory;
import org.dromara.neutrinoproxy.core.aot.NeutrinoCoreRuntimeNativeRegistrar;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Configuration
public class ProxyConfiguration extends ProxyMessageFactory {

    @Override
    public void stop() {
        Solon.stop();
    }

    @Override
    public boolean isAotRuntime() {
        return NativeDetector.isAotRuntime();
    }

    @Bean
    public NeutrinoCoreRuntimeNativeRegistrar neutrinoCoreRuntimeNativeRegistrar(){
        return new NeutrinoCoreRuntimeNativeRegistrar();
    }
}
