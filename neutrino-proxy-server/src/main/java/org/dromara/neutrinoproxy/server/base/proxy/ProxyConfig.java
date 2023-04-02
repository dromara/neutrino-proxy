package org.dromara.neutrinoproxy.server.base.proxy;

import lombok.Data;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 服务端代理配置
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Data
@Component
public class ProxyConfig {
	/**
	 * 传输协议相关配置
	 */
	@Inject("${neutrino.proxy.protocol}")
	private Protocol protocol;
	/**
	 * 服务端配置
	 */
	@Inject("${neutrino.proxy.server}")
	private Server server;

	@Data
	public static class Protocol {
		private Integer maxFrameLength;
		private Integer lengthFieldOffset;
		private Integer lengthFieldLength;
		private Integer initialBytesToStrip;
		private Integer lengthAdjustment;
		private Integer readIdleTime;
		private Integer writeIdleTime;
		private Integer allIdleTimeSeconds;
	}

	@Data
	public static class Server {
		private Integer port;
		private Integer sslPort;
		private String keyStorePassword;
		private String keyManagerPassword;
		private String jksPath;
		private Integer bossThreadCount;
		private Integer workThreadCount;
	}

}
