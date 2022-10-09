package fun.asgc.neutrino.proxy.client.config;

import fun.asgc.neutrino.core.annotation.Bean;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Order;
import fun.asgc.neutrino.core.base.DefaultDispatcher;
import fun.asgc.neutrino.core.base.Dispatcher;
import fun.asgc.neutrino.core.base.Ordered;
import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ProxyConfiguration {

    @Bean
    public Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher() {
        return new DefaultDispatcher<>("消息调度器",
                BeanManager.getBeanListBySuperClass(ProxyMessageHandler.class),
                proxyMessage -> ProxyDataTypeEnum.of((int)proxyMessage.getType()) == null ? null : ProxyDataTypeEnum.of((int)proxyMessage.getType()).getName());
    }
}
