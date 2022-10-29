/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fun.asgc.neutrino.proxy.client.core;

import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.base.CustomThreadFactory;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import fun.asgc.neutrino.proxy.client.util.ProxyUtil;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageDecoder;
import fun.asgc.neutrino.proxy.core.ProxyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

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
@NonIntercept
@Component
public class ProxyClientService {
	@Autowired
	private ProxyConfig proxyConfig;
	@Autowired("bootstrap")
	private static Bootstrap bootstrap;
	@Autowired("realServerBootstrap")
	private static Bootstrap realServerBootstrap;
	private static NioEventLoopGroup workerGroup;
	@Autowired
	private Environment environment;
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

		workerGroup = new NioEventLoopGroup();
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
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				if (proxyConfig.getClient().getSslEnable()) {
					ch.pipeline().addLast(createSslHandler());
				}

				ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
						proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
						proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
				ch.pipeline().addLast(new ProxyMessageEncoder());
				ch.pipeline().addLast(new IdleStateHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
				ch.pipeline().addLast(new ClientChannelHandler());
			}
		});
	}

	public void start() {
		if (StringUtil.isEmpty(proxyConfig.getLicenseKey())) {
			return;
		}
		if (null == channel || !channel.isActive()) {
			try {
				connectProxyServer();
			} catch (Exception e) {
				log.error("启动异常", e);
			}
		} else {
			channel.writeAndFlush(ProxyMessage.buildAuthMessage(proxyConfig.getLicenseKey()));
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
						future.channel().writeAndFlush(ProxyMessage.buildAuthMessage(proxyConfig.getLicenseKey()));
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
		if (!reconnectServiceEnable) {
			return;
		}
		if (null != channel && channel.isActive()) {
			return;
		}
		log.info("客户端重连 seq:{}", ++reconnectCount);
		try {
			connectProxyServer();
		} catch (Exception e) {
			log.error("重连异常", e);
		}
		System.out.println("1");
	}

	@Bean
	public Bootstrap bootstrap() {
		return new Bootstrap();
	}

	@Bean
	public Bootstrap realServerBootstrap() {
		return new Bootstrap();
	}
}
