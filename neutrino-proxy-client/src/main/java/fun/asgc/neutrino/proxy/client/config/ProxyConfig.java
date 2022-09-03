/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fun.asgc.neutrino.proxy.client.config;

import fun.asgc.neutrino.core.annotation.Configuration;
import fun.asgc.neutrino.core.annotation.Init;
import fun.asgc.neutrino.core.annotation.Value;
import lombok.Data;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Data
@Configuration(prefix = "neutrino.proxy")
public class ProxyConfig {
	public static ProxyConfig instance;
	private Protocol protocol;
	private Client client;
	private String licenseKey;

	@Data
	public static class Protocol {
		@Value("max-frame-length")
		private Integer maxFrameLength;
		@Value("length-field-offset")
		private Integer lengthFieldOffset;
		@Value("length-field-length")
		private Integer lengthFieldLength;
		@Value("initial-bytes-to-strip")
		private Integer initialBytesToStrip;
		@Value("length-adjustment")
		private Integer lengthAdjustment;
		@Value("read-idle-time")
		private Integer readIdleTime;
		@Value("write-idle-time")
		private Integer writeIdleTime;
		@Value("all-idle-time-seconds")
		private Integer allIdleTimeSeconds;
	}

	@Data
	public static class Client {
		@Value("key-store-password")
		private String keyStorePassword;
		@Value("jks-path")
		private String jksPath;
		@Value("server-ip")
		private String serverIp;
		@Value("server-port")
		private Integer serverPort;
		@Value("ssl-enable")
		private Boolean sslEnable;
	}

	@Init
	public void init() {
		instance = this;
	}
}
