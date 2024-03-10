package org.dromara.neutrinoproxy.server.proxy.enhance;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.util.FileUtil;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.proxy.core.BytesMetricsHandler;
import org.dromara.neutrinoproxy.server.proxy.core.ProxyTunnelServer;
import org.dromara.neutrinoproxy.server.proxy.security.HttpVisitorSecurityChannelHandler;
import org.dromara.neutrinoproxy.server.proxy.security.VisitorFlowLimiterChannelHandler;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * 应用加载完成事件（即启动完成）- 判断是否配置域名-配置了域名则启动HTTPS代理
 * 默认端口 443
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Component
public class HttpsProxy implements EventListener<AppLoadEndEvent> {
    @Inject
    private ProxyConfig proxyConfig;
    /**
     * NioServerSocketChannel对应的future
     */
    private ChannelFuture httpsFuture;
    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        if (null == proxyConfig.getServer().getTcp().getHttpsProxyPort() ||
            StringUtils.isEmpty(proxyConfig.getServer().getTcp().getJksPath()) ||
            StringUtils.isEmpty(proxyConfig.getServer().getTcp().getKeyStorePassword())) {
            log.info("no config domain name,nonsupport https proxy.");
            return;
        }
        this.start();
    }

    private void start() {
        // 处理网络连接---接受请求
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 进行socketChannel的网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    if (null != proxyConfig.getServer().getTcp().getTransferLogEnable() && proxyConfig.getServer().getTcp().getTransferLogEnable()) {
                        ch.pipeline().addFirst(new LoggingHandler(HttpsProxy.class));
                    }
                    ch.pipeline().addLast(createSslHandler())
                        .addFirst(new BytesMetricsHandler())
                        .addLast(new HttpVisitorSecurityChannelHandler())
                        .addLast("flowLimiter", new VisitorFlowLimiterChannelHandler())
                        .addLast(new HttpVisitorChannelHandler(ch));
                }
            });
            httpsFuture = bootstrap.bind("0.0.0.0", proxyConfig.getServer().getTcp().getHttpsProxyPort()).sync();
            log.info("Https proxy server started！port:{}", proxyConfig.getServer().getTcp().getHttpsProxyPort());
            //添加关闭重启的监听器，3秒后尝试重启
//            httpsFuture.channel().closeFuture().addListener(genericFutureListener);
//            httpsFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("https proxy start err!", e);
        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//            httpsFuture.channel().close();
        }
    }

    private ChannelHandler createSslHandler() {
        try {
            InputStream jksInputStream = FileUtil.getInputStream(proxyConfig.getServer().getTcp().getJksPath());
            SSLContext serverContext = SSLContext.getInstance("TLS");
            final KeyStore ks = KeyStore.getInstance("JKS");

            ks.load(jksInputStream, proxyConfig.getServer().getTcp().getKeyStorePassword().toCharArray());
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, proxyConfig.getServer().getTcp().getKeyStorePassword().toCharArray());
            TrustManager[] trustManagers = null;

            serverContext.init(kmf.getKeyManagers(), trustManagers, null);

            SSLEngine sslEngine = serverContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);

            return new SslHandler(sslEngine);
        } catch (Exception e) {
            log.error("create SSL handler failed", e);
            e.printStackTrace();
        }
        return null;
    }
}
