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
package org.dromara.neutrinoproxy.client.util;

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
public class ProxyUtil {
	private static final AttributeKey<Boolean> USER_CHANNEL_WRITEABLE = AttributeKey.newInstance("user_channel_writeable");

	private static final AttributeKey<Boolean> CLIENT_CHANNEL_WRITEABLE = AttributeKey.newInstance("client_channel_writeable");

	private static final int MAX_POOL_SIZE = 100;

	private static Map<String, Channel> realServerChannels = new ConcurrentHashMap<String, Channel>();

	private static ConcurrentLinkedQueue<Channel> proxyChannelPool = new ConcurrentLinkedQueue<Channel>();

	private static volatile Channel cmdChannel;

	private static String clientId;
	private static final String CLIENT_ID_FILE = ".NEUTRINO_PROXY_CLIENT_ID";

	public static void borrowProxyChanel(Bootstrap proxyTunnelBootstrap, final ProxyChannelBorrowListener borrowListener) {
		Channel channel = proxyChannelPool.poll();
		if (null != channel) {
			borrowListener.success(channel);
			return;
		}

		proxyTunnelBootstrap.connect().addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				borrowListener.success(future.channel());
			} else {
				borrowListener.error(future.cause());
			}
		});
	}

	public static void returnProxyChanel(Channel proxyChanel) {
		if (proxyChannelPool.size() > MAX_POOL_SIZE) {
			proxyChanel.close();
		} else {
			proxyChanel.config().setOption(ChannelOption.AUTO_READ, true);
			proxyChanel.attr(Constants.NEXT_CHANNEL).remove();
			proxyChannelPool.offer(proxyChanel);
		}
	}

	public static void removeProxyChanel(Channel proxyChanel) {
		proxyChannelPool.remove(proxyChanel);
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
		if (StringUtils.isNotBlank(proxyConfig.getClient().getClientId())) {
			clientId = proxyConfig.getClient().getClientId();
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
}
