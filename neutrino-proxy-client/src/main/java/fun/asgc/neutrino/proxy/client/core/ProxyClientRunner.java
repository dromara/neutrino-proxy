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

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Bean;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import fun.asgc.neutrino.proxy.client.util.ClientChannelMannager;
import fun.asgc.neutrino.proxy.core.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Component
public class ProxyClientRunner implements ApplicationRunner {
	@Autowired
	private ProxyConfig proxyConfig;
	@Autowired("bootstrap")
	private static Bootstrap bootstrap;
	@Autowired("realServerBootstrap")
	private static Bootstrap realServerBootstrap;
	private static NioEventLoopGroup workerGroup;

	@Override
	public void run(String[] args) {
		ProxyClientConfig clientConfig = getClientConfig(args.length >= 1 ? args[args.length - 1] : null);
		proxyConfig.setClientConfig(clientConfig);
		connectProxyServer();
	}

	/**
	 * 连接代理服务器
	 */
	private void connectProxyServer() {
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
				ch.pipeline().addLast(new IdleCheckHandler(proxyConfig.getProtocol().getReadIdleTime(), proxyConfig.getProtocol().getWriteIdleTime(), proxyConfig.getProtocol().getAllIdleTimeSeconds()));
				ch.pipeline().addLast(new ClientChannelHandler());
			}
		});
		bootstrap.connect(proxyConfig.getClient().getServerIp(), proxyConfig.getClient().getServerPort())
			.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {

						// 连接成功，向服务器发送客户端认证信息（clientKey）
						ClientChannelMannager.setCmdChannel(future.channel());
						future.channel().writeAndFlush(ProxyMessage.buildAuthMessage(JSONObject.toJSONString(proxyConfig.getClientConfig())));
						log.info("连接代理服务成功.");
					} else {
						log.info("连接代理服务失败!");
					}
				}
			});
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

	@Bean
	public Bootstrap bootstrap() {
		return new Bootstrap();
	}

	@Bean
	public Bootstrap realServerBootstrap() {
		return new Bootstrap();
	}

	private ProxyClientConfig getClientConfig(String path) {
		if (StringUtil.isEmpty(path)) {
			path = "./config.json";
		}
		String content = FileUtil.readContentAsString(path);
		if (StringUtil.isEmpty(content)) {
			log.error("配置文件: {} 不存在或格式异常!", path);
			System.exit(0);
			return null;
		}
		try {
			ProxyClientConfig clientConfig = JSONObject.parseObject(content, ProxyClientConfig.class);
			if (StringUtil.isEmpty(clientConfig.getClientKey()) || CollectionUtil.isEmpty(clientConfig.getProxy())) {
				log.error("配置异常!");
				System.exit(0);
				return null;
			}
			return clientConfig;
		} catch (Exception e) {
			log.error("解析配置文件异常!", e);
		}
		return null;
	}
}
