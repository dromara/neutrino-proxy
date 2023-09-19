package org.dromara.neutrinoproxy.client.config;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.dromara.neutrinoproxy.client.core.CmdChannelHandler;
import org.dromara.neutrinoproxy.client.core.ProxyChannelHandler;
import org.dromara.neutrinoproxy.client.core.RealServerChannelHandler;
import org.dromara.neutrinoproxy.client.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.*;
import org.dromara.neutrinoproxy.core.dispatcher.DefaultDispatcher;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;

import java.net.InetSocketAddress;
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

    @Bean("tunnelWorkGroup")
    public NioEventLoopGroup tunnelWorkGroup(@Inject ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getTunnel().getThreadCount());
    }

    @Bean("tcpRealServerWorkGroup")
    public NioEventLoopGroup tcpRealServerWorkGroup(@Inject ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return new NioEventLoopGroup(proxyConfig.getTunnel().getThreadCount());
    }

    @Bean("cmdTunnelBootstrap")
    public Bootstrap cmdTunnelBootstrap(@Inject ProxyConfig proxyConfig,
                                        @Inject("tunnelWorkGroup") NioEventLoopGroup tunnelWorkGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(tunnelWorkGroup);
        bootstrap.channel(NioSocketChannel.class);
//		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
//		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
//		/**
//		 * TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。（一个连接会设置MSS参数，因此，TCP/IP希望每次都能够以MSS尺寸的数据块来发送数据）。
//		 * Nagle算法就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
//		 */
//		bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.remoteAddress(InetSocketAddress.createUnresolved(proxyConfig.getTunnel().getServerIp(), proxyConfig.getTunnel().getServerPort()));

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (proxyConfig.getTunnel().getSslEnable()) {
                    ch.pipeline().addLast(ProxyUtil.createSslHandler(proxyConfig));
                }
                if (null != proxyConfig.getTunnel().getTransferLogEnable() && proxyConfig.getTunnel().getTransferLogEnable()) {
                    ch.pipeline().addFirst(new LoggingHandler(CmdChannelHandler.class));
                }
                ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
                        proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
                        proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
                ch.pipeline().addLast(new ProxyMessageEncoder());
                ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
                ch.pipeline().addLast(new CmdChannelHandler());
            }
        });
        return bootstrap;
    }

    @Bean("tcpProxyTunnelBootstrap")
    public Bootstrap tcpProxyTunnelBootstrap(@Inject ProxyConfig proxyConfig,
                                             @Inject("tunnelWorkGroup") NioEventLoopGroup tunnelWorkGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(tunnelWorkGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(InetSocketAddress.createUnresolved(proxyConfig.getTunnel().getServerIp(), proxyConfig.getTunnel().getServerPort()));
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (proxyConfig.getTunnel().getSslEnable()) {
                    ch.pipeline().addLast(ProxyUtil.createSslHandler(proxyConfig));
                }
                if (null != proxyConfig.getTunnel().getTransferLogEnable() && proxyConfig.getTunnel().getTransferLogEnable()) {
                    ch.pipeline().addFirst(new LoggingHandler(ProxyChannelHandler.class));
                }
                ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
                        proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
                        proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
                ch.pipeline().addLast(new ProxyMessageEncoder());
                ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
                ch.pipeline().addLast(new ProxyChannelHandler());
            }
        });
        return bootstrap;
    }

    @Bean("udpProxyTunnelBootstrap")
    private Bootstrap udpProxyTunnelBootstrap() {
        Bootstrap bootstrap = new Bootstrap();

        return bootstrap;
    }

    @Bean("realServerBootstrap")
    public Bootstrap realServerBootstrap(@Inject ProxyConfig proxyConfig,
                                             @Inject("tcpRealServerWorkGroup") NioEventLoopGroup tcpRealServerWorkGroup
                                             ) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(tcpRealServerWorkGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                if (null != proxyConfig.getTunnel().getTransferLogEnable() && proxyConfig.getTunnel().getTransferLogEnable()) {
                    ch.pipeline().addFirst(new LoggingHandler(RealServerChannelHandler.class));
                }
                ch.pipeline().addLast(new RealServerChannelHandler());
            }
        });
        return bootstrap;
    }


}
