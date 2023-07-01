package org.dromara.neutrinoproxy.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.solonplugins.job.annotation.EnableJob;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.web.cors.CrossFilter;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@EnableJob
@SolonMain
public class ProxyServer {

	public static void main(String[] args) {
		Solon.start(ProxyServer.class, args, app -> {
			// 跨域支持。加-1 优先级更高
			app.filter(-1, new CrossFilter().allowedOrigins("*"));
			// 设置日志级别
			setLogLevel(app);
		});
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
