package org.dromara.neutrinoproxy.client;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@SolonMain
public class ProxyClient {

	public static void main(String[] args) {
		Solon.start(ProxyClient.class, args, app -> {
			setAlias("neutrino.proxy.tunnel.serverIp", "serverIp");
			setAlias("neutrino.proxy.tunnel.serverPort", "serverPort");
			setAlias("neutrino.proxy.tunnel.sslEnable", "sslEnable");
			setAlias("neutrino.proxy.tunnel.jksPath", "jksPath");
			setAlias("neutrino.proxy.tunnel.keyStorePassword", "keyStorePassword");
			setAlias("neutrino.proxy.tunnel.licenseKey", "licenseKey");
		});
	}

	/**
	 * 别名处理，支持较短的启动参数名
	 * @param key
	 * @param alias
	 */
	private static void setAlias(String key, String alias) {
		String val = Solon.cfg().argx().get(alias);
		if (StrUtil.isNotBlank(val)) {
			Solon.cfg().put(key, val);
		}
	}

}
