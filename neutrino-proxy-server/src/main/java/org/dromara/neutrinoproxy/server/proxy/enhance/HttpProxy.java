package org.dromara.neutrinoproxy.server.proxy.enhance;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.proxy.core.BytesMetricsHandler;
import org.dromara.neutrinoproxy.server.proxy.core.ProxyTunnelServer;
import org.dromara.neutrinoproxy.server.proxy.security.HttpVisitorSecurityChannelHandler;
import org.dromara.neutrinoproxy.server.proxy.security.VisitorFlowLimiterChannelHandler;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

/**
 * 应用加载完成事件（即启动完成）- 判断是否配置域名-配置了域名则启动HTTP代理
 * 默认端口 80
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Component
public class HttpProxy implements EventListener<AppLoadEndEvent> {
    @Inject
    private ProxyConfig proxyConfig;

    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
//        StrUtil.isBlank(proxyConfig.getServer().getTcp().getDomainName()) ||
        if (null == proxyConfig.getServer().getTcp().getHttpProxyPort()) {
            log.info("no config domain name,nonsupport http proxy.");
            return;
        }
        this.start();
    }

    private void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            if (null != proxyConfig.getServer().getTcp().getTransferLogEnable() &&
                                proxyConfig.getServer().getTcp().getTransferLogEnable()) {
                                ch.pipeline().addFirst(new LoggingHandler(HttpProxy.class));
                            }
                            ch.pipeline().addFirst(new BytesMetricsHandler());
                            ch.pipeline().addLast(new HttpVisitorSecurityChannelHandler());
                            ch.pipeline().addLast("flowLimiter",new VisitorFlowLimiterChannelHandler());
                            ch.pipeline().addLast(new HttpVisitorChannelHandler());
                        }
                    });
            bootstrap.bind("0.0.0.0", proxyConfig.getServer().getTcp().getHttpProxyPort()).sync();
            log.info("Http proxy server start success！port:{}", proxyConfig.getServer().getTcp().getHttpProxyPort());
        } catch (Exception e) {
            log.error("http proxy start err!", e);
        }
    }
}
