package fun.asgc.neutrino.proxy.client;

import cn.hutool.core.util.StrUtil;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.SolonMain;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@SolonMain
public class ProxyClient {

	public static void main(String[] args) {
		Solon.start(ProxyClient.class, args, app -> {
			setAlias("neutrino.proxy.client.serverIp", "serverIp");
			setAlias("neutrino.proxy.client.serverPort", "serverPort");
			setAlias("neutrino.proxy.client.sslEnable", "sslEnable");
			setAlias("neutrino.proxy.client.jksPath", "jksPath");
			setAlias("neutrino.proxy.client.keyStorePassword", "keyStorePassword");
			setAlias("neutrino.proxy.client.licenseKey", "licenseKey");
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
