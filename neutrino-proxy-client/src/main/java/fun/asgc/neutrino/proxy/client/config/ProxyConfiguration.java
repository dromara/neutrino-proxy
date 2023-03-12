package fun.asgc.neutrino.proxy.client.config;

import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.DefaultDispatcher;
import fun.asgc.neutrino.proxy.core.dispatcher.Dispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
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

    @Bean("bootstrap")
    public Bootstrap bootstrap() {
        return new Bootstrap();
    }

    @Bean("realServerBootstrap")
    public Bootstrap realServerBootstrap() {
        return new Bootstrap();
    }

}
