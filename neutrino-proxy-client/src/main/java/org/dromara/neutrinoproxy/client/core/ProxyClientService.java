package org.dromara.neutrinoproxy.client.core;

import cn.hutool.core.util.StrUtil;
import io.netty.handler.logging.LoggingHandler;
import org.dromara.neutrinoproxy.client.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageDecoder;
import org.dromara.neutrinoproxy.core.ProxyMessageEncoder;
import org.dromara.neutrinoproxy.core.util.FileUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端服务
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Component
public class ProxyClientService {
	@Inject
	private ProxyConfig proxyConfig;
	@Inject("bootstrap")
	private Bootstrap bootstrap;
	@Inject("realServerBootstrap")
	private Bootstrap realServerBootstrap;
	private volatile Channel channel;
	/**
	 * 重连间隔（秒）
	 */
	private static final long RECONNECT_INTERVAL_SECONDS = 5;
	/**
	 * 重连次数
	 */
	private volatile int reconnectCount = 0;
	/**
	 * 启用重连服务
	 */
	private volatile boolean reconnectServiceEnable = false;
	/**
	 * 重连服务执行器
	 */
	private static final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory("ClientReconnect"));

	@Init
	public void init() {
		this.reconnectExecutor.scheduleWithFixedDelay(this::reconnect, 0, RECONNECT_INTERVAL_SECONDS, TimeUnit.SECONDS);

		NioEventLoopGroup workerGroup = new NioEventLoopGroup(proxyConfig.getClient().getThreadCount());
		realServerBootstrap.group(workerGroup);
		realServerBootstrap.channel(NioSocketChannel.class);
		realServerBootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new RealServerChannelHandler());
			}
		});

		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		/**
		 * TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。（一个连接会设置MSS参数，因此，TCP/IP希望每次都能够以MSS尺寸的数据块来发送数据）。
		 * Nagle算法就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
		 */
		bootstrap.option(ChannelOption.TCP_NODELAY, true);

		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				if (proxyConfig.getClient().getSslEnable()) {
					ch.pipeline().addLast(createSslHandler());
				}
//				ch.pipeline().addFirst(new LoggingHandler(ProxyClientService.class));
				ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
						proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
						proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
				ch.pipeline().addLast(new ProxyMessageEncoder());
				ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
				ch.pipeline().addLast(new ClientChannelHandler());
			}
		});
		this.start();
	}

	public void start() {
		if (StrUtil.isEmpty(proxyConfig.getClient().getServerIp())) {
			log.error("not found server-ip config.");
			Solon.stop();
			return;
		}
		if (null == proxyConfig.getClient().getServerPort()) {
			log.error("not found server-port config.");
			Solon.stop();
			return;
		}
		if (null != proxyConfig.getClient().getSslEnable() && proxyConfig.getClient().getSslEnable()
			&& StrUtil.isEmpty(proxyConfig.getClient().getJksPath())) {
			log.error("not found jks-path config.");
			Solon.stop();
			return;
		}
		if (StrUtil.isEmpty(proxyConfig.getClient().getLicenseKey())) {
			log.error("not found license-key config.");
			Solon.stop();
			return;
		}
		if (null == channel || !channel.isActive()) {
			try {
				connectProxyServer();
			} catch (Exception e) {
				log.error("client start error", e);
			}
		} else {
			channel.writeAndFlush(ProxyMessage.buildAuthMessage(proxyConfig.getClient().getLicenseKey()));
		}
	}

	/**
	 * 连接代理服务器
	 */
	private void connectProxyServer() throws InterruptedException {
		bootstrap.connect(proxyConfig.getClient().getServerIp(), proxyConfig.getClient().getServerPort())
			.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						channel = future.channel();
						// 连接成功，向服务器发送客户端认证信息（licenseKey）
						ProxyUtil.setCmdChannel(future.channel());
						future.channel().writeAndFlush(ProxyMessage.buildAuthMessage(proxyConfig.getClient().getLicenseKey()));
						log.info("连接代理服务成功. channelId:{}", future.channel().id().asLongText());

						reconnectServiceEnable = true;
						reconnectCount = 0;
					} else {
						log.info("连接代理服务失败!");
					}
				}
			}).sync();
	}

	private ChannelHandler createSslHandler() {
		try {
			InputStream jksInputStream = FileUtil.getInputStream(proxyConfig.getClient().getJksPath());

			SSLContext clientContext = SSLContext.getInstance("TLS");
			final KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(jksInputStream, proxyConfig.getClient().getKeyStorePassword().toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			TrustManager[] trustManagers = tmf.getTrustManagers();
			clientContext.init(null, trustManagers, null);

			SSLEngine sslEngine = clientContext.createSSLEngine();
			sslEngine.setUseClientMode(true);

			return new SslHandler(sslEngine);
		} catch (Exception e) {
			log.error("创建SSL处理器失败", e);
			e.printStackTrace();
		}
		return null;
	}

	protected synchronized void reconnect() {
		if (!reconnectServiceEnable || null == channel) {
			return;
		}
		if (channel.isActive()) {
			return;
		}
		channel.close();
		log.info("客户端重连 seq:{}", ++reconnectCount);
		try {
			connectProxyServer();
		} catch (Exception e) {
			log.error("重连异常", e);
		}
	}
}
