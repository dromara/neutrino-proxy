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
	private static final Map<Integer, String> proxyInfoMap = new HashMap<>();
	/**
	 * 服务端口 -> 指令通道映射
	 */
	private static Map<Integer, Channel> serverPortToCmdChannelMap = new ConcurrentHashMap<>();
	/**
	 * license -> 指令通道映射
	 */
	private static Map<Integer, Channel> licenseToCmdChannelMap = new ConcurrentHashMap<>();

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
		for (ProxyMapping proxyMapping : proxyMappingList) {
			licenseToServerPortMap.get(licenseId).add(proxyMapping.getServerPort());
			proxyInfoMap.put(proxyMapping.getServerPort(), proxyMapping.getLanInfo());
		}
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
		if (CollectionUtil.isEmpty(serverPorts)) {
			return;
		}

		for (int port : serverPorts) {
			serverPortToCmdChannelMap.put(port, cmdChannel);
		}

		setAttachInfo(cmdChannel, new CmdChannelAttachInfo()
			.setIp(ChannelUtil.getIP(cmdChannel))
			.setServerPorts(serverPorts)
			.setLicenseId(licenseId)
			.setVisitorChannelMap(new HashMap<>(16)));
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
	 * @param userId
	 * @param userChannel
	 */
	public static void addUserChannelToCmdChannel(Channel cmdChannel, String userId, Channel userChannel) {
		InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
		String lanInfo = getClientLanInfoByServerPort(sa.getPort());
		setAttachInfo(userChannel, new VisitorChannelAttachInfo()
			.setVisitorId(userId)
			.setLanInfo(lanInfo)
			.setIp(ChannelUtil.getIP(userChannel))
		);
		userChannelMapLock.writeLock().lock();
		try {
			((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getVisitorChannelMap().put(userId, userChannel);
		} finally {
			userChannelMapLock.writeLock().unlock();
		}
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
	 * @param userId
	 * @return
	 */
	public static Channel getUserChannel(Channel cmdChannel, String userId) {
		if (null == cmdChannel || null == getAttachInfo(cmdChannel)) {
			return null;
		}
		return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getVisitorChannelMap().get(userId);
	}

	/**
	 * 获取用户编号
	 *
	 * @param visitorChannel
	 * @return
	 */
	public static String getVisitorChannelUserId(Channel visitorChannel) {
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
