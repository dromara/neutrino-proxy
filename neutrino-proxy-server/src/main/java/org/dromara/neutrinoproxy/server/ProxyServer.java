package org.dromara.neutrinoproxy.server;

import lombok.extern.slf4j.Slf4j;
import org.dromara.solonplugins.job.annotation.EnableJob;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.web.cors.CrossFilter;

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
            String loglevel = System.getenv("LOG_LEVEL");
            if (Utils.isNotEmpty(loglevel)) {
                app.cfg().put("solon.logging.logger.root.level", loglevel);
            }
		});
	}
}
