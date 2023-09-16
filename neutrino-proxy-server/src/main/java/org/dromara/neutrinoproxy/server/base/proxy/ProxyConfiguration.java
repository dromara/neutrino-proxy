package org.dromara.neutrinoproxy.server.base.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
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
import org.dromara.neutrinoproxy.server.proxy.core.TcpVisitorChannelHandler;
import org.dromara.neutrinoproxy.server.proxy.core.UdpVisitorChannelHandler;
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

    @Bean("tcpServerBossGroup")
    public NioEventLoopGroup tcpServerBossGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getServer().getTcp().getBossThreadCount());
    }

    @Bean("tcpServerWorkerGroup")
    public NioEventLoopGroup tcpServerWorkerGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getServer().getTcp().getWorkThreadCount());
    }

    @Bean("tcpServerBootstrap")
    public ServerBootstrap tcpServerBootstrap(@Inject("tcpServerBossGroup") NioEventLoopGroup tcpServerBossGroup,
                                              @Inject("tcpServerWorkerGroup") NioEventLoopGroup tcpServerWorkerGroup,
                                              @Inject ProxyConfig proxyConfig
    ) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(tcpServerBossGroup, tcpServerWorkerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            if (null != proxyConfig.getServer().getTcp().getTransferLogEnable() && proxyConfig.getServer().getTcp().getTransferLogEnable()) {
                ch.pipeline().addFirst(new LoggingHandler(TcpVisitorChannelHandler.class));
            }
            ch.pipeline().addFirst(new BytesMetricsHandler());
            ch.pipeline().addLast(new TcpVisitorChannelHandler());
            }
        });
        return bootstrap;
    }

    @Bean("udpServerBossGroup")
    private NioEventLoopGroup udpBossGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getServer().getUdp().getBossThreadCount());
    }

    @Bean("udpServerWorkerGroup")
    private NioEventLoopGroup udpWorkerGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getServer().getUdp().getWorkThreadCount());
    }

    @Bean("udpServerBootstrap")
    public Bootstrap udpBootstrap(@Inject("udpServerBossGroup") NioEventLoopGroup udpServerBossGroup,
                                  @Inject("udpServerWorkerGroup") NioEventLoopGroup udpServerWorkerGroup,
                                  @Inject ProxyConfig proxyConfig) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(udpServerBossGroup)
            // 主线程处理
            .channel(NioDatagramChannel.class)
            // 广播
            .option(ChannelOption.SO_BROADCAST, true)
            // 设置读缓冲区为2M
            .option(ChannelOption.SO_RCVBUF, 2048 * 1024)
            // 设置写缓冲区为1M
            .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
            .handler(new ChannelInitializer<NioDatagramChannel>() {
                @Override
                protected void initChannel(NioDatagramChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (null != proxyConfig.getServer().getUdp().getTransferLogEnable() && proxyConfig.getServer().getUdp().getTransferLogEnable()) {
                        ch.pipeline().addFirst(new LoggingHandler(UdpVisitorChannelHandler.class));
                    }
                    pipeline.addLast(udpServerWorkerGroup, new UdpVisitorChannelHandler());
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
