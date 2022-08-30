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
package fun.asgc.neutrino.proxy.server.proxy.domain;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
@Accessors(chain = true)
@Data
public class CmdChannelAttachInfo {
	/**
	 * 用户通道映射
	 */
	private Map<String, Channel> userChannelMap;
	/**
	 * 客户端信息
	 */
	private String clientLanInfo;
	/**
	 * 服务端端口集合
	 */
	private Set<Integer> serverPorts;
	/**
	 * licenseKey
	 */
	private String licenseKey;
}
