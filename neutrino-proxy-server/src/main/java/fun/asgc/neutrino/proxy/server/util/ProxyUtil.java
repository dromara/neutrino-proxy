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

import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.core.ChannelAttribute;
import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.server.proxy.domain.CmdChannelAttachInfo;
import fun.asgc.neutrino.proxy.server.proxy.domain.ProxyMapping;
import fun.asgc.neutrino.proxy.server.proxy.domain.UserChannelAttachInfo;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
	private static final Map<String, Set<Integer>> licenseToServerPortMap = new HashMap<>();
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
	private static Map<String, Channel> licenseToCmdChannelMap = new ConcurrentHashMap<>();

	/**
	 * 初始化代理信息
	 * @param licenseKey 客户端licenseKey
	 * @param proxyMappingList 代理映射集合
	 */
	public static void initProxyInfo(String licenseKey, List<ProxyMapping> proxyMappingList) {
		if (StringUtil.isEmpty(licenseKey)) {
			return;
		}
		licenseToServerPortMap.put(licenseKey, new HashSet<>());
		if (CollectionUtil.isEmpty(proxyMappingList)) {
			return;
		}
		for (ProxyMapping proxyMapping : proxyMappingList) {
			licenseToServerPortMap.get(licenseKey).add(proxyMapping.getServerPort());
			proxyInfoMap.put(proxyMapping.getServerPort(), proxyMapping.getLanInfo());
		}
	}

	/**
	 * 根据licenseKey获取服务端端口集合
	 * @param licenseKey 客户端licenseKey
	 * @return 服务端端口集合
	 */
	public static Set<Integer> getServerPortsByLicenseKey(String licenseKey) {
		return licenseToServerPortMap.get(licenseKey);
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
	 * @param licenseKey licenseKey
	 * @param cmdChannel 指令通道
	 * @param serverPorts 服务端端口集合
	 */
	public static void addCmdChannel(String licenseKey, Channel cmdChannel, Set<Integer> serverPorts) {
		if (CollectionUtil.isEmpty(serverPorts)) {
			return;
		}

		// 客户端（proxy-client）相对较少，这里同步的比较重 TODO 后续优化
		// 保证服务器对外端口与客户端到服务器的连接关系在临界情况时调用removeChannel(Channel channel)时不出问题
		synchronized (serverPortToCmdChannelMap) {
			for (int port : serverPorts) {
				serverPortToCmdChannelMap.put(port, cmdChannel);
			}
		}

		setAttachInfo(cmdChannel, new CmdChannelAttachInfo()
			.setServerPorts(serverPorts)
			.setLicenseKey(licenseKey)
			.setUserChannelMap(new HashMap<>(16)));
		licenseToCmdChannelMap.put(licenseKey, cmdChannel);
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
		Channel channel0 = licenseToCmdChannelMap.remove(cmdChannelAttachInfo.getLicenseKey());
		if (cmdChannel != channel0) {
			licenseToCmdChannelMap.put(cmdChannelAttachInfo.getLicenseKey(), cmdChannel);
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

		Map<String, Channel> userChannels = cmdChannelAttachInfo.getUserChannelMap();
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

	public static Channel getCmdChannelByLicenseKey(String licenseKey) {
		return licenseToCmdChannelMap.get(licenseKey);
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
		setAttachInfo(userChannel, new UserChannelAttachInfo()
			.setUserId(userId)
			.setLanInfo(lanInfo)
		);
		((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getUserChannelMap().put(userId, userChannel);
	}

	public static Channel removeUserChannelFromCmdChannel(Channel cmdChannel, String userId) {
		if (null == getAttachInfo(cmdChannel) || null == ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getUserChannelMap().get(userId)) {
			return null;
		}

		synchronized (cmdChannel) {
			return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getUserChannelMap().remove(userId);
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
		return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getUserChannelMap().get(userId);
	}

	/**
	 * 获取用户编号
	 *
	 * @param userChannel
	 * @return
	 */
	public static String getUserChannelUserId(Channel userChannel) {
		if (null == userChannel || null == getAttachInfo(userChannel)) {
			return null;
		}
		return ((UserChannelAttachInfo)getAttachInfo(userChannel)).getUserId();
	}

	/**
	 * 获取代理控制客户端连接绑定的所有用户连接
	 *
	 * @param cmdChannel
	 * @return
	 */
	public static Map<String, Channel> getUserChannels(Channel cmdChannel) {
		if (null == cmdChannel || null == getAttachInfo(cmdChannel)) {
			return null;
		}
		return ((CmdChannelAttachInfo)getAttachInfo(cmdChannel)).getUserChannelMap();
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
