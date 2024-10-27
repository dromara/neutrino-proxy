package org.dromara.neutrinoproxy.client.util;

import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.client.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.core.ProxyChannelBorrowListener;
import org.dromara.neutrinoproxy.core.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import org.dromara.neutrinoproxy.core.util.FileUtil;
import org.noear.solon.Solon;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 代理工具
 * @author: aoshiguchen
 * @date: 2022/8/31
 */
@Slf4j
public class ProxyUtil {
	private static final AttributeKey<Boolean> USER_CHANNEL_WRITEABLE = AttributeKey.newInstance("user_channel_writeable");

	private static final AttributeKey<Boolean> CLIENT_CHANNEL_WRITEABLE = AttributeKey.newInstance("client_channel_writeable");

	private static final int MAX_POOL_SIZE = 100;

	private static Map<String, Channel> realServerChannels = new ConcurrentHashMap<String, Channel>();

	private static ConcurrentLinkedQueue<Channel> tcpProxyChannelPool = new ConcurrentLinkedQueue<Channel>();
	private static ConcurrentLinkedQueue<Channel> udpProxyChannelPool = new ConcurrentLinkedQueue<>();

	private static volatile Channel cmdChannel;

	private static String clientId;
	private static final String CLIENT_ID_FILE = ".NEUTRINO_PROXY_CLIENT_ID";

	public static void borrowTcpProxyChanel(Bootstrap tcpProxyTunnelBootstrap, final ProxyChannelBorrowListener borrowListener) {
		Channel channel = tcpProxyChannelPool.poll();
		if (null != channel) {
			borrowListener.success(channel);
			return;
		}

		tcpProxyTunnelBootstrap.connect().addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				borrowListener.success(future.channel());
			} else {
				borrowListener.error(future.cause());
			}
		});
	}

	public static void returnTcpProxyChanel(Channel proxyChanel) {
		if (tcpProxyChannelPool.size() > MAX_POOL_SIZE) {
			proxyChanel.close();
		} else {
			proxyChanel.config().setOption(ChannelOption.AUTO_READ, true);
			proxyChanel.attr(Constants.NEXT_CHANNEL).remove();
			tcpProxyChannelPool.offer(proxyChanel);
		}
	}



	public static void removeTcpProxyChanel(Channel proxyChanel) {
		tcpProxyChannelPool.remove(proxyChanel);
	}

	public static void borrowUdpProxyChanel(Bootstrap tcpProxyTunnelBootstrap, final ProxyChannelBorrowListener borrowListener) {
		Channel channel = udpProxyChannelPool.poll();
		if (null != channel) {
			borrowListener.success(channel);
			return;
		}

		tcpProxyTunnelBootstrap.connect().addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				borrowListener.success(future.channel());
			} else {
				borrowListener.error(future.cause());
			}
		});
	}

	public static void returnUdpProxyChanel(Channel proxyChanel) {
		if (udpProxyChannelPool.size() > MAX_POOL_SIZE) {
			proxyChanel.close();
		} else {
			proxyChanel.config().setOption(ChannelOption.AUTO_READ, true);
			proxyChanel.attr(Constants.NEXT_CHANNEL).remove();
			udpProxyChannelPool.offer(proxyChanel);
		}
	}



	public static void removeUdpProxyChanel(Channel proxyChanel) {
		udpProxyChannelPool.remove(proxyChanel);
	}

	public static void setCmdChannel(Channel cmdChannel) {
		ProxyUtil.cmdChannel = cmdChannel;
	}

	public static Channel getCmdChannel() {
		return cmdChannel;
	}

	public static void setRealServerChannelVisitorId(Channel realServerChannel, String visitorId) {
		realServerChannel.attr(Constants.VISITOR_ID).set(visitorId);
	}

	public static String getVisitorIdByRealServerChannel(Channel realServerChannel) {
		return realServerChannel.attr(Constants.VISITOR_ID).get();
	}

	public static Channel getRealServerChannel(String userId) {
		return realServerChannels.get(userId);
	}

	public static void addRealServerChannel(String userId, Channel realServerChannel) {
		realServerChannels.put(userId, realServerChannel);
	}

	public static Channel removeRealServerChannel(String userId) {
		return realServerChannels.remove(userId);
	}

	public static boolean isRealServerReadable(Channel realServerChannel) {
		return realServerChannel.attr(CLIENT_CHANNEL_WRITEABLE).get() && realServerChannel.attr(USER_CHANNEL_WRITEABLE).get();
	}

	public static void clearRealServerChannels() {
		Iterator<Map.Entry<String, Channel>> ite = realServerChannels.entrySet().iterator();
		while (ite.hasNext()) {
			Channel realServerChannel = ite.next().getValue();
			if (realServerChannel.isActive()) {
				realServerChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			}
		}

		realServerChannels.clear();
	}

	public static String getClientId() {
		if (StringUtils.isNotBlank(clientId)) {
			return clientId;
		}
		ProxyConfig proxyConfig = Solon.context().getBean(ProxyConfig.class);
		if (StringUtils.isNotBlank(proxyConfig.getTunnel().getClientId())) {
			clientId = proxyConfig.getTunnel().getClientId();
			return clientId;
		}
		String id = FileUtil.readContentAsString(CLIENT_ID_FILE);
		if (StringUtils.isNotBlank(id)) {
			clientId = id;
			return id;
		}
		id = UUID.randomUUID().toString().replace("-", "");
		FileUtil.write(CLIENT_ID_FILE, id);
		clientId = id;
		return id;
	}

	public static ChannelHandler createSslHandler(ProxyConfig proxyConfig) {
		try {
			InputStream jksInputStream = FileUtil.getInputStream(proxyConfig.getTunnel().getJksPath());

			SSLContext clientContext = SSLContext.getInstance("TLS");
			final KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(jksInputStream, proxyConfig.getTunnel().getKeyStorePassword().toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			TrustManager[] trustManagers = tmf.getTrustManagers();
			clientContext.init(null, trustManagers, null);

			SSLEngine sslEngine = clientContext.createSSLEngine();
			sslEngine.setUseClientMode(true);

			return new SslHandler(sslEngine);
		} catch (Exception e) {
			log.error("create SSL handler failed", e);
			e.printStackTrace();
		}
		return null;
	}

}
