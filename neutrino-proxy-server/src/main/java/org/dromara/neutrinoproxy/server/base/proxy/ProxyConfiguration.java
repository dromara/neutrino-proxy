package org.dromara.neutrinoproxy.server.base.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.DefaultDispatcher;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import org.dromara.neutrinoproxy.server.proxy.core.BytesMetricsHandler;
import org.dromara.neutrinoproxy.server.proxy.core.ProxyTunnelServer;
import org.dromara.neutrinoproxy.server.proxy.core.TcpVisitorChannelHandler;
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

    @Bean("tcpServerBootstrap")
    public ServerBootstrap tcpServerBootstrap(@Inject("serverBossGroup") NioEventLoopGroup serverBossGroup,
                                              @Inject("serverWorkerGroup") NioEventLoopGroup serverWorkerGroup,
                                              @Inject ProxyConfig proxyConfig
    ) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(serverBossGroup, serverWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (null != proxyConfig.getServer().getTransferLogEnable() && proxyConfig.getServer().getTransferLogEnable()) {
                    ch.pipeline().addFirst(new LoggingHandler(ProxyTunnelServer.class));
                }
                ch.pipeline().addFirst(new BytesMetricsHandler());
                ch.pipeline().addLast(new TcpVisitorChannelHandler());
            }
        });
        return bootstrap;
    }

    @Bean("tunnelBossGroup")
    public NioEventLoopGroup tunnelBossGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getTunnel().getBossThreadCount());
    }

    @Bean("tunnelWorkerGroup")
    public NioEventLoopGroup tunnelWorkerGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getTunnel().getWorkThreadCount());
    }

}
