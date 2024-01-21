package org.dromara.neutrinoproxy.client.sdk.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.dromara.neutrinoproxy.client.sdk.core.*;
import org.dromara.neutrinoproxy.client.sdk.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.ProxyMessageDecoder;
import org.dromara.neutrinoproxy.core.ProxyMessageEncoder;
import org.dromara.neutrinoproxy.core.aot.NeutrinoCoreRuntimeNativeRegistrar;

import java.net.InetSocketAddress;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */

public abstract class IProxyConfiguration {

    public abstract IBeanHandler getBeanHandler();

    public NioEventLoopGroup tunnelWorkGroup(ProxyConfig proxyConfig) {
        return new NioEventLoopGroup(proxyConfig.getTunnel().getThreadCount());
    }

    public NioEventLoopGroup tcpRealServerWorkGroup(ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return new NioEventLoopGroup(proxyConfig.getTunnel().getThreadCount());
    }

    public NioEventLoopGroup udpServerGroup(ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return new NioEventLoopGroup(proxyConfig.getClient().getUdp().getBossThreadCount());
    }

    public NioEventLoopGroup udpWorkGroup(ProxyConfig proxyConfig) {
        // 暂时先公用此配置
        return new NioEventLoopGroup(proxyConfig.getClient().getUdp().getWorkThreadCount());
    }

    public Bootstrap cmdTunnelBootstrap(ProxyConfig proxyConfig,
                                        NioEventLoopGroup tunnelWorkGroup) {
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
                ch.pipeline().addLast(new CmdChannelHandler(getBeanHandler()));
            }
        });
        return bootstrap;
    }

    public Bootstrap tcpProxyTunnelBootstrap(ProxyConfig proxyConfig,
                                             NioEventLoopGroup tunnelWorkGroup) {
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
                    ch.pipeline().addFirst(new LoggingHandler(TcpProxyChannelHandler.class));
                }
                ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
                        proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
                        proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
                ch.pipeline().addLast(new ProxyMessageEncoder());
                ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
                ch.pipeline().addLast(new TcpProxyChannelHandler(getBeanHandler()));
            }
        });
        return bootstrap;
    }
    public Bootstrap udpProxyTunnelBootstrap(ProxyConfig proxyConfig,
                                              NioEventLoopGroup tunnelWorkGroup) {
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
                    ch.pipeline().addFirst(new LoggingHandler(TcpProxyChannelHandler.class));
                }
                ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
                        proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
                        proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
                ch.pipeline().addLast(new ProxyMessageEncoder());
                ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
                ch.pipeline().addLast(new UdpProxyChannelHandler(getBeanHandler()));
            }
        });
        return bootstrap;
    }

    public Bootstrap realServerBootstrap(ProxyConfig proxyConfig,
                                             NioEventLoopGroup tcpRealServerWorkGroup
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

    public Bootstrap udpServerBootstrap(ProxyConfig proxyConfig,
                                        NioEventLoopGroup udpServerGroup,
                                         NioEventLoopGroup udpWorkGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(udpServerGroup)
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
                        if (null != proxyConfig.getClient().getUdp().getTransferLogEnable() && proxyConfig.getClient().getUdp().getTransferLogEnable()) {
                            ch.pipeline().addFirst(new LoggingHandler(UdpRealServerHandler.class));
                        }
                        pipeline.addLast(udpWorkGroup, new UdpRealServerHandler());
                    }
                });
        return bootstrap;
    }

//    public NeutrinoCoreRuntimeNativeRegistrar neutrinoCoreRuntimeNativeRegistrar() {
//        return new NeutrinoCoreRuntimeNativeRegistrar();
//    }

}
