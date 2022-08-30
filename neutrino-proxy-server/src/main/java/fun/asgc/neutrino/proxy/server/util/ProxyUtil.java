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
import fun.asgc.neutrino.proxy.server.proxy.domain.ProxyMapping;

import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
public class ProxyUtil {
	/**
	 * license -> 服务端口映射
	 */
	private static final Map<String, Set<Integer>> licenseToServerPortMap = new HashMap<>();
	/**
	 * 代理信息映射
	 */
	private static final Map<Integer, String> proxyInfoMap = new HashMap<>();

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
}
