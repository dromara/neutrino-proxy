package org.dromara.neutrinoproxy.client.starter.config;

import lombok.Data;
import org.dromara.neutrinoproxy.client.starter.ssh.SSHProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
@Data
@ConfigurationProperties(prefix = "neutrino.proxy")
@Component
public class SpringProxyConfig {
    private Boolean enable=false;
    private Boolean sshEnable=false;
	private Protocol protocol;
	private Tunnel tunnel;
	private Client client;
    private List<SSHProxy> sshProxys;

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
	public static class Tunnel {
		private String keyStorePassword;
		private String jksPath;
		private String serverIp;
		private Integer serverPort;
		private Boolean sslEnable;
		private Integer obtainLicenseInterval;
		private String licenseKey;
		private Integer threadCount;
		private String clientId;
		private Boolean transferLogEnable;
		private Boolean heartbeatLogEnable;
		private Reconnection reconnection;
	}

	@Data
	public static class Client {
//		private Tcp tcp;
		private Udp udp;
	}

	@Data
	public static class Reconnection {
		private Integer intervalSeconds;
		private Boolean unlimited;
	}

	@Data
	public static class Tcp {

	}

	@Data
	public static class Udp {
		private Integer bossThreadCount;
		private Integer workThreadCount;
		private String puppetPortRange;
		private Boolean transferLogEnable;
	}
}
