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
package fun.asgc.neutrino.proxy.server.base.proxy;

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
	}

}
