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

package fun.asgc.neutrino.proxy.server.core;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Bean;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.proxy.core.IdleCheckHandler;
import fun.asgc.neutrino.proxy.core.ProxyMessageDecoder;
import fun.asgc.neutrino.proxy.core.ProxyMessageEncoder;
import fun.asgc.neutrino.proxy.server.config.ProxyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@NonIntercept
@Component
public class ProxyServerRunner implements ApplicationRunner {
	@Autowired
	private ProxyConfig proxyConfig;
	@Autowired("serverBossGroup")
	private NioEventLoopGroup serverBossGroup;
	@Autowired("serverWorkerGroup")
	private NioEventLoopGroup serverWorkerGroup;

	@Override
	public void run(String[] args) {
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
			bootstrap.bind(proxyConfig.getServer().getPort()).sync();
			log.info("代理服务启动，端口：{}", proxyConfig.getServer().getPort());
		} catch (Exception e) {
			log.error("代理服务异常", e);
		}
	}

	private void startProxyServerForSSL() {
		if (null == proxyConfig.getServer().getSslPort()) {
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
			bootstrap.bind(proxyConfig.getServer().getSslPort()).sync();
			log.info("代理服务启动，SSL端口： {}", proxyConfig.getServer().getSslPort());
		} catch (Exception e) {
			log.error("代理服务异常", e);
		}
	}

	private ChannelHandler createSslHandler() {
		try {
			InputStream jksInputStream = FileUtil.getInputStream(proxyConfig.getServer().getJksPath());
			SSLContext serverContext = SSLContext.getInstance("TLS");
			final KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(jksInputStream, proxyConfig.getServer().getKeyStorePassword().toCharArray());
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, proxyConfig.getServer().getKeyManagerPassword().toCharArray());
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
		ch.pipeline().addLast(new ProxyMessageDecoder(proxyConfig.getProtocol().getMaxFrameLength(),
			proxyConfig.getProtocol().getLengthFieldOffset(), proxyConfig.getProtocol().getLengthFieldLength(),
			proxyConfig.getProtocol().getLengthAdjustment(), proxyConfig.getProtocol().getInitialBytesToStrip()));
		ch.pipeline().addLast(new ProxyMessageEncoder());
		ch.pipeline().addLast(new IdleCheckHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
		ch.pipeline().addLast(new ServerChannelHandler());
	}

	@Bean
	public NioEventLoopGroup serverBossGroup() {
		return new NioEventLoopGroup();
	}

	@Bean
	public NioEventLoopGroup serverWorkerGroup() {
		return new NioEventLoopGroup();
	}
}
