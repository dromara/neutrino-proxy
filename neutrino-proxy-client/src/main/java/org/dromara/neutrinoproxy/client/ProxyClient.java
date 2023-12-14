package org.dromara.neutrinoproxy.client;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
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
            String loglevel = System.getenv("LOG_LEVEL");
            if (Utils.isNotEmpty(loglevel)) {
                app.cfg().put("solon.logging.logger.root.level", loglevel);
            }

			setAlias("neutrino.proxy.tunnel.server-ip", "serverIp");
			setAlias("neutrino.proxy.tunnel.server-port", "serverPort");
			setAlias("neutrino.proxy.tunnel.ssl-enable", "sslEnable");
			setAlias("neutrino.proxy.tunnel.jks-path", "jksPath");
			setAlias("neutrino.proxy.tunnel.key-store-password", "keyStorePassword");
			setAlias("neutrino.proxy.tunnel.license-key", "licenseKey");

            log.info("NeutrinoProxy Client ：{}", app.cfg().get("solon.app.version"));
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
