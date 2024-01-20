package org.dromara.neutrinoproxy.client.sdk.config;

import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;

public interface IBeanHandler {
     Dispatcher getDispatcher();
     ProxyConfig getProxyConfig();
}
