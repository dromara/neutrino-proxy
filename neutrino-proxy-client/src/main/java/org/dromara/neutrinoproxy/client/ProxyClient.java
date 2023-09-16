package org.dromara.neutrinoproxy.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.SolonMain;
import org.slf4j.LoggerFactory;

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
			// 设置日志级别
			setLogLevel(app);
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

	private static void setLogLevel(SolonApp app) {
		String loggerLevel = app.cfg().get("neutrino.proxy.logger.level");
		if (StringUtils.isBlank(loggerLevel)) {
			return;
		}

		try {
			Level level = Level.toLevel(loggerLevel);
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			for (Logger logger : loggerContext.getLoggerList()) {
				logger.setLevel(level);
			}
		} catch (Exception e) {
			log.error("日志级别设置失败", e);
		}
	}
}
