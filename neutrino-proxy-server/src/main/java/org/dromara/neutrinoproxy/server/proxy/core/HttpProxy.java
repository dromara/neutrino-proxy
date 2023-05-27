package org.dromara.neutrinoproxy.server.proxy.core;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Component
public class HttpProxy implements EventListener<AppLoadEndEvent> {
    @Inject("serverBossGroup")
    private NioEventLoopGroup serverBossGroup;
    @Inject("serverWorkerGroup")
    private NioEventLoopGroup serverWorkerGroup;
    @Inject
    private ProxyConfig proxyConfig;
    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        if (StrUtil.isBlank(proxyConfig.getServer().getDomainName()) || null == proxyConfig.getServer().getHttpProxyPort()) {
            log.info("no config domain name,nonsupport http proxy.");
            return;
        }
        this.start();
    }

    private void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(serverBossGroup, serverWorkerGroup)
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addFirst(new BytesMetricsHandler());
                            ch.pipeline().addLast(new HttpVisitorChannelHandler(proxyConfig.getServer().getDomainName()));
                        }
                    });
            bootstrap.bind("0.0.0.0", proxyConfig.getServer().getHttpProxyPort()).sync();
            log.info("Http代理服务启动成功！");
        } catch (Exception e) {
            log.error("http proxy start err!", e);
        }
    }
}
