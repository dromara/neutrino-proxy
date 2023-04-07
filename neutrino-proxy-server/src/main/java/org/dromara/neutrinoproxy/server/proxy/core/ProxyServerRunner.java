package org.dromara.neutrinoproxy.server.proxy.core;

import io.netty.handler.logging.LoggingHandler;
import org.dromara.neutrinoproxy.core.ProxyMessageDecoder;
import org.dromara.neutrinoproxy.core.ProxyMessageEncoder;
import org.dromara.neutrinoproxy.core.util.FileUtil;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.io.InputStream;
import java.security.KeyStore;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Component
public class ProxyServerRunner implements EventListener<AppLoadEndEvent> {
	@Inject
	private ProxyConfig proxyConfig;
	@Inject("serverBossGroup")
	private NioEventLoopGroup serverBossGroup;
	@Inject("serverWorkerGroup")
	private NioEventLoopGroup serverWorkerGroup;
	@Inject("${neutrino.proxy.server.port}")
	private Integer port;
	@Inject("${neutrino.proxy.server.ssl-port}")
	private Integer sslPort;
	@Inject("${neutrino.proxy.server.jks-path}")
	private String jksPath;
	@Inject("${neutrino.proxy.server.key-store-password}")
	private String keyStorePassword;
	@Inject("${neutrino.proxy.server.key-manager-password}")
	private String keyManagerPassword;
	@Override
	public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
		startProxyServer();
		startProxyServerForSSL();
	}
	/**
	 * 启动代理服务
	 */
	private void startProxyServer() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(serverBossGroup, serverWorkerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				proxyServerCommonInitHandler(ch);
			}
		});
		try {
			bootstrap.bind(port).sync();
			log.info("代理服务启动，端口：{}", port);
		} catch (Exception e) {
			log.error("代理服务异常", e);
		}
	}

	private void startProxyServerForSSL() {
		if (null == sslPort) {
			return;
		}
 		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(serverBossGroup, serverWorkerGroup)
			.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(createSslHandler());
				proxyServerCommonInitHandler(ch);
			}
		});
		try {
			bootstrap.bind(sslPort).sync();
			log.info("代理服务启动，SSL端口： {}", sslPort);
		} catch (Exception e) {
			log.error("代理服务异常", e);
		}
	}

	private ChannelHandler createSslHandler() {
		try {
			InputStream jksInputStream = FileUtil.getInputStream(jksPath);
			SSLContext serverContext = SSLContext.getInstance("TLS");
			final KeyStore ks = KeyStore.getInstance("JKS");

			ks.load(jksInputStream, keyStorePassword.toCharArray());
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, keyManagerPassword.toCharArray());
			TrustManager[] trustManagers = null;

			serverContext.init(kmf.getKeyManagers(), trustManagers, null);

			SSLEngine sslEngine = serverContext.createSSLEngine();
			sslEngine.setUseClientMode(false);
			sslEngine.setNeedClientAuth(false);

			return new SslHandler(sslEngine);
		} catch (Exception e) {
			log.error("创建SSL处理器失败", e);
			e.printStackTrace();
		}
		return null;
	}

	private void proxyServerCommonInitHandler(SocketChannel ch) {
//		ch.pipeline().addFirst(new LoggingHandler(ProxyServerRunner.class));
		ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
			proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
			proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
		ch.pipeline().addLast(new ProxyMessageEncoder());
		ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
		ch.pipeline().addLast(new ServerChannelHandler());
	}
}
