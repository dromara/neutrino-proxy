package org.dromara.neutrinoproxy.server.base.proxy;

import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.DefaultDispatcher;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;

import java.util.List;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Configuration
public class ProxyConfiguration implements LifecycleBean {

    @Override
    public void start() throws Throwable {
        List<ProxyMessageHandler> list = Solon.context().getBeansOfType(ProxyMessageHandler.class);
        Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher = new DefaultDispatcher<>("消息调度器", list,
                proxyMessage -> ProxyDataTypeEnum.of((int)proxyMessage.getType()) == null ?
                        null : ProxyDataTypeEnum.of((int)proxyMessage.getType()).getName());

        Solon.context().wrapAndPut(Dispatcher.class, dispatcher);
    }

    @Bean("serverBossGroup")
    public NioEventLoopGroup serverBossGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getServer().getBossThreadCount());
    }

    @Bean("serverWorkerGroup")
    public NioEventLoopGroup serverWorkerGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getServer().getWorkThreadCount());
    }

}
