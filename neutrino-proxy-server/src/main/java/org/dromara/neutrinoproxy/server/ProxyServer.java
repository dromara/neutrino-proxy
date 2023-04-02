package org.dromara.neutrinoproxy.server;

import fun.asgc.solon.extend.job.annotation.EnableJob;
import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.web.cors.CrossFilter;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@EnableJob
@SolonMain
public class ProxyServer {

	public static void main(String[] args) {
		Solon.start(ProxyServer.class, args, app -> {
			// 跨域支持。加-1 优先级更高
			app.filter(-1, new CrossFilter().allowedOrigins("*"));
		});
	}
}
