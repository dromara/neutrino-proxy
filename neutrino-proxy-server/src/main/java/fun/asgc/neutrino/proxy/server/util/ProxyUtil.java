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
package fun.asgc.neutrino.proxy.server.util;

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.util.ChannelUtil;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.core.ChannelAttribute;
import fun.asgc.neutrino.proxy.server.proxy.domain.CmdChannelAttachInfo;
import fun.asgc.neutrino.proxy.server.proxy.domain.ProxyMapping;
import fun.asgc.neutrino.proxy.server.proxy.domain.VisitorChannelAttachInfo;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
public class ProxyUtil {
	public static final AttributeKey<ChannelAttribute> CHANNEL_ATTR_KEY = AttributeKey.valueOf("netty.channel.attr");
	/**
	 * license -> 服务端口映射
	 */
	private static final Map<Integer, Set<Integer>> licenseToServerPortMap = new HashMap<>();
	/**
	 * 代理信息映射
	 */
	private static final Map<Integer, String> proxyInfoMap = new ConcurrentHashMap<>();
	/**
	 * 服务端口 -> 指令通道映射
	 */
	private static Map<Integer, Channel> serverPortToCmdChannelMap = new ConcurrentHashMap<>();
	/**
	 * license -> 指令通道映射
	 */
	private static Map<Integer, Channel> licenseToCmdChannelMap = new ConcurrentHashMap<>();
	/**
	 * 服务端口 -> 访问通道映射
	 */
	private static Map<Integer, Channel> serverPortToVisitorChannel = new ConcurrentHashMap<>();

	/**
	 * cmdChannelAttachInfo.getUserChannelMap() 读写锁
	 */
	private static final ReadWriteLock userChannelMapLock = new ReentrantReadWriteLock();

	/**
	 * 初始化代理信息
	 * @param licenseId licenseId
	 * @param proxyMappingList 代理映射集合
	 */
	public static void initProxyInfo(Integer licenseId, List<ProxyMapping> proxyMappingList) {
		licenseToServerPortMap.put(licenseId, new HashSet<>());
		addProxyInfo(licenseId, proxyMappingList);
	}

	public static void addProxyInfo(Integer licenseId, List<ProxyMapping> proxyMappingList) {
		if (!CollectionUtil.isEmpty(proxyMappingList)) {
			for (ProxyMapping proxyMapping : proxyMappingList) {
				licenseToServerPortMap.get(licenseId).add(proxyMapping.getServerPort());
				proxyInfoMap.put(proxyMapping.getServerPort(), proxyMapping.getLanInfo());
			}
		}
	}

	public static void addProxyInfo(Integer licenseId, ProxyMapping proxyMapping) {
		if (null == licenseId || null == proxyMapping) {
			return;
		}
		licenseToServerPortMap.get(licenseId).add(proxyMapping.getServerPort());
		proxyInfoMap.put(proxyMapping.getServerPort(), proxyMapping.getLanInfo());
	}

	public static void removeProxyInfo(Integer serverPort) {
		proxyInfoMap.remove(serverPort);
	}

	/**
	 * 根据licenseId获取服务端端口集合
	 * @param licenseId licenseId
	 * @return 服务端端口集合
	 */
	public static Set<Integer> getServerPortsByLicenseKey(Integer licenseId) {
		return licenseToServerPortMap.get(licenseId);
	}

	/**
	 * 根据服务端端口获取客户端代理信息
	 * @param serverPort 服务端端口
	 * @return 客户端代理信息
	 */
	public static String getClientLanInfoByServerPort(Integer serverPort) {
		return proxyInfoMap.get(serverPort);
	}

	/**
	 * 添加指令通道相关缓存信息
	 * @param licenseId licenseId
	 * @param cmdChannel 指令通道
	 * @param serverPorts 服务端端口集合
	 */
	public static void addCmdChannel(Integer licenseId, Channel cmdChannel, Set<Integer> serverPorts) {
		if (!CollectionUtil.isEmpty(serverPorts)) {
			for (int port : serverPorts) {
				serverPortToCmdChannelMap.put(port, cmdChannel);
			}
		}
		CmdChannelAttachInfo cmdChannelAttachInfo = getAttachInfo(cmdChannel);
		if (null == cmdChannelAttachInfo) {
			cmdChannelAttachInfo = new CmdChannelAttachInfo()
					.setIp(ChannelUtil.getIP(cmdChannel))
					.setLicenseId(licenseId)
					.setVisitorChannelMap(new HashMap<>(16))
					.setServerPorts(Sets.newHashSet());
			setAttachInfo(cmdChannel, cmdChannelAttachInfo);
		}

		if (!CollectionUtil.isEmpty(serverPorts)) {
			cmdChannelAttachInfo.getServerPorts().addAll(serverPorts);
		}

		licenseToCmdChannelMap.put(licenseId, cmdChannel);
	}

	/**
	 * 删除指令通道相关缓存信息
	 * @param cmdChannel 指令通道
	 */
	public static void removeCmdChannel(Channel cmdChannel) {
		if (null == cmdChannel || null == getAttachInfo(cmdChannel)) {
			return;
		}

		CmdChannelAttachInfo cmdChannelAttachInfo = getAttachInfo(cmdChannel);
		Channel channel0 = licenseToCmdChannelMap.remove(cmdChannelAttachInfo.getLicenseId());
		if (cmdChannel != channel0) {
			licenseToCmdChannelMap.put(cmdChannelAttachInfo.getLicenseId(), cmdChannel);
		}

		for (int port : cmdChannelAttachInfo.getServerPorts()) {
			Channel proxyChannel = serverPortToCmdChannelMap.remove(port);
			if (proxyChannel == null) {
				continue;
			}

			// 在执行断连之前新的连接已经连上来了
			if (proxyChannel != cmdChannel) {
				serverPortToCmdChannelMap.put(port, proxyChannel);
			}
		}

		if (cmdChannel.isActive()) {
			cmdChannel.close();
		}

		Map<String, Channel> userChannels = cmdChannelAttachInfo.getVisitorChannelMap();
		Iterator<String> ite = userChannels.keySet().iterator();
		while (ite.hasNext()) {
			Channel userChannel = userChannels.get(ite.next());
			if (userChannel.isActive()) {
				userChannel.close();
			}
		}
	}

	public static Channel getCmdChannelByServerPort(Integer serverPort) {
		return serverPortToCmdChannelMap.get(serverPort);
	}

	public static Channel getCmdChannelByLicenseId(Integer licenseId) {
		return licenseToCmdChannelMap.get(licenseId);
	}

	/**
	 * 增加用户连接与代理客户端连接关系
	 *
	 * @param visitorId
	 * @param visitorChannel
	 */
	public static void addVisitorChannelToCmdChannel(Channel cmdChannel, String visitorId, Channel visitorChannel, Integer serverPort) {
		InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
		String lanInfo = getClientLanInfoByServerPort(sa.getPort());
		CmdChannelAttachInfo cmdChannelAttachInfo = getAttachInfo(cmdChannel);

		setAttachInfo(visitorChannel, new VisitorChannelAttachInfo()
			.setVisitorId(visitorId)
			.setLanInfo(lanInfo)
			.setServerPort(serverPort)
			.setLicenseId(cmdChannelAttachInfo.getLicenseId())
			.setIp(ChannelUtil.getIP(visitorChannel))
		);
		userChannelMapLock.writeLock().lock();
		try {
			cmdChannelAttachInfo.getVisitorChannelMap().put(visitorId, visitorChannel);
		} finally {
			userChannelMapLock.writeLock().unlock();
		}
		serverPortToVisitorChannel.put(serverPort, visitorChannel);
	}

	public static Channel removeVisitorChannelFromCmdChannel(Channel cmdChannel, String visitorId) {
		if (null == getAttachInfo(cmdChannel) || null == ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getVisitorChannelMap().get(visitorId)) {
			return null;
		}

		userChannelMapLock.writeLock().lock();
		try {
			return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getVisitorChannelMap().remove(visitorId);
		} finally {
			userChannelMapLock.writeLock().unlock();
		}
	}

	/**
	 * 根据代理客户端连接与用户编号获取用户连接
	 *
	 * @param visitorId
	 * @return
	 */
	public static Channel getVisitorChannel(Channel cmdChannel, String visitorId) {
		if (null == cmdChannel || null == getAttachInfo(cmdChannel)) {
			return null;
		}
		return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getVisitorChannelMap().get(visitorId);
	}

	/**
	 * 根据服务端口获取访问通道
	 * @param serverPort
	 * @return
	 */
	public static Channel getVisitorChannelByServerPort(Integer serverPort) {
		return serverPortToVisitorChannel.get(serverPort);
	}

	/**
	 * 获取访问者ID
	 *
	 * @param visitorChannel
	 * @return
	 */
	public static String getVisitorIdByChannel(Channel visitorChannel) {
		if (null == visitorChannel || null == getAttachInfo(visitorChannel)) {
			return null;
		}
		return ((VisitorChannelAttachInfo)getAttachInfo(visitorChannel)).getVisitorId();
	}

	/**
	 * 获取代理控制客户端连接绑定的所有用户连接
	 *
	 * @param cmdChannel
	 * @return
	 */
	public static Map<String, Channel> getVisitorChannels(Channel cmdChannel) {
		if (null == cmdChannel || null == getAttachInfo(cmdChannel)) {
			return null;
		}
		return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getVisitorChannelMap();
	}

	private static void setAttachInfo(Channel channel, Object obj) {
		if (null == channel) {
			return;
		}
		channel.attr(CHANNEL_ATTR_KEY).set(ChannelAttribute.create()
			.set("attachInfo", obj)
		);
	}

	public static <T> T getAttachInfo(Channel channel) {
		if (null == channel || null == channel.attr(CHANNEL_ATTR_KEY).get()) {
			return null;
		}
		return channel.attr(CHANNEL_ATTR_KEY).get().get("attachInfo");
	}
}
