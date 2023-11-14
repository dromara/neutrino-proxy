package org.dromara.neutrinoproxy.server.util;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.ChannelAttribute;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.proxy.domain.CmdChannelAttachInfo;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyAttachment;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyMapping;
import org.dromara.neutrinoproxy.server.proxy.domain.VisitorChannelAttachInfo;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
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
	private static final Map<Integer, Channel> serverPortToCmdChannelMap = new ConcurrentHashMap<>();
	/**
	 * license -> 指令通道映射
	 */
	private static final Map<Integer, Channel> licenseToCmdChannelMap = new ConcurrentHashMap<>();
	/**
	 * 服务端口 -> 访问通道映射
	 */
	private static final Map<Integer, Channel> serverPortToVisitorChannel = new ConcurrentHashMap<>();

	/**
	 * cmdChannelAttachInfo.getUserChannelMap() 读写锁
	 */
	private static final ReadWriteLock userChannelMapLock = new ReentrantReadWriteLock();
	/**
	 * 访问者ID生成器
	 */
	private static final AtomicLong visitorIdProducer = new AtomicLong(0);
	/**
	 * 代理 - connect附加映射
	 */
	private static final Map<String, ProxyAttachment> proxyConnectAttachmentMap = new HashMap<>();
	/**
	 * 子域名 - 服务端端口映射
	 */
	private static final Map<String, Integer> subdomainToServerPort = new HashMap<>();
	/**
	 * licenseId - 客户端Id映射
	 */
	private static final Map<Integer, String> licenseIdToClientIdMap = new HashMap<>();

    private static final Map<Integer, byte[]> licenseIdToSecureKeyMap = new ConcurrentHashMap<>();

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
					.setIp(((InetSocketAddress)cmdChannel.remoteAddress()).getAddress().getHostAddress())
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
	public static void addVisitorChannelToCmdChannel(NetworkProtocolEnum protocol, Channel cmdChannel, String visitorId, Channel visitorChannel, Integer serverPort) {
		InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
		String lanInfo = getClientLanInfoByServerPort(sa.getPort());
		CmdChannelAttachInfo cmdChannelAttachInfo = getAttachInfo(cmdChannel);

		VisitorChannelAttachInfo attachInfo = new VisitorChannelAttachInfo()
				.setProtocol(protocol)
				.setVisitorId(visitorId)
				.setLanInfo(lanInfo)
				.setServerPort(serverPort)
				.setLicenseId(cmdChannelAttachInfo.getLicenseId());
		if (NetworkProtocolEnum.UDP != protocol) {
			attachInfo.setIp(((InetSocketAddress)visitorChannel.remoteAddress()).getAddress().getHostAddress());
		}
		setAttachInfo(visitorChannel, attachInfo);
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

	/**
	 * 为访问者连接产生ID
	 *
	 * @return
	 */
	public static String newVisitorId() {
		return String.valueOf(visitorIdProducer.incrementAndGet());
	}

	/**
	 * 添加代理附加对象
	 * @param visitorId
	 * @param proxyAttachment
	 */
	public static void addProxyConnectAttachment(String visitorId, ProxyAttachment proxyAttachment) {
		proxyConnectAttachmentMap.put(visitorId, proxyAttachment);
	}

	/**
	 * 获取代理附加对象
	 * @param visitorId
	 * @return
	 */
	public static ProxyAttachment getProxyConnectAttachment(String visitorId) {
		return proxyConnectAttachmentMap.get(visitorId);
	}

	/**
	 * 删除代理附加对象
	 * @param visitorId
	 */
	public static void remoteProxyConnectAttachment(String visitorId) {
		proxyConnectAttachmentMap.remove(visitorId);
	}

	/**
	 * 设置子域名到服务端端口的映射
	 * @param subdomain
	 * @param serverPort
	 */
	public static void setSubdomainToServerPort(String subdomain, Integer serverPort) {
		subdomainToServerPort.put(subdomain, serverPort);
	}

	/**
	 * 删除子域名到服务端端口的映射
	 * @param subdomain
	 */
	public static void removeSubdomainToServerPort(String subdomain) {
		subdomainToServerPort.remove(subdomain);
	}

	/**
	 * 根据子域名获取外网端口
	 * @param subdomain
	 * @return
	 */
	public static Integer getServerPortBySubdomain(String subdomain) {
		return subdomainToServerPort.get(subdomain);
	}

	/**
	 * 关闭http响应channel
	 * @param channel
	 * @return
	 */
	public static void closeHttpProxyResponseChannel(Channel channel) {
		if (null == channel) {
			return;
		}

		String visitorId = getVisitorIdByChannel(channel); // channel.attr(Constants.VISITOR_ID).get();
		if (StringUtils.isBlank(visitorId)) {
			return;
		}
		ProxyAttachment proxyAttachment = ProxyUtil.getProxyConnectAttachment(visitorId);
		if (null != proxyAttachment) {
			tryClose(channel);
		}
	}

	/**
	 * 关闭channel
	 * @param channel
	 */
	public static void tryClose(Channel channel) {
		try {
			channel.close();
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * 设置licenseId - clientId映射
	 * @param licenseId
	 * @param clientId
	 */
	public static void setLicenseIdToClientIdMap(Integer licenseId, String clientId) {
		licenseIdToClientIdMap.put(licenseId, clientId);
	}

	/**
	 * 根据licenseId获取clientId
	 * @param licenseId
	 * @return
	 */
	public static String getClientIdByLicenseId(Integer licenseId) {
		return licenseIdToClientIdMap.get(licenseId);
	}

	/**
	 * 根据licenseId删除clientId
	 * @param licenseId
	 */
	public static void removeClientIdByLicenseId(Integer licenseId) {
		licenseIdToClientIdMap.remove(licenseId);
	}

    public static void setSecureKey(Integer licenseId, byte[] key) {
        licenseIdToSecureKeyMap.put(licenseId, key);
    }

    public static void setLicenseIdRelativeProxyChannelSecurity(Integer licenseId) {
        Set<Integer> portSet = licenseToServerPortMap.get(licenseId);
        for(Integer port : portSet) {
            // TODO 代理客户端
        }
    }

    public static void setChannelSecurity(Integer licenseId, Channel channel) {
        if (channel != null && licenseIdToSecureKeyMap.containsKey(licenseId)) {
            channel.attr(Constants.IS_SECURITY).set(true);
            channel.attr(Constants.SECURE_KEY).set(licenseIdToSecureKeyMap.get(licenseId));
        }
    }
}
