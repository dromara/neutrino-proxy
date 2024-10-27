package org.dromara.neutrinoproxy.core;

import lombok.Data;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Data
public class ProxyClientConfig {
	private String environment;
	private String clientKey;
	private List<Proxy> proxy;

	@Data
	public static class Proxy {
		private Integer serverPort;
		private String clientInfo;
	}
}
