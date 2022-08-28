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
package fun.asgc.neutrino.proxy.server.util;

import fun.asgc.neutrino.core.web.context.HttpRequestWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/2
 */
public class HttpUtil {

	/**
	 * 获取客户端IP
	 */
	public static String getIP(ChannelHandlerContext context, HttpRequestWrapper request) {
		String ip = request.getHeaderValue("clientip"); // for UC browser
		if (ip == null) {
			ip = request.getHeaderValue("X-Real-IP");
			if (ip == null) {
				ip = request.getHeaderValue("X-Forwarded-For");
				if (ip == null) {
					ip = context.channel().remoteAddress().toString();
					if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
						//根据网卡取本机配置的IP
						InetAddress inet = null;
						try {
							inet = InetAddress.getLocalHost();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						ip = inet.getHostAddress();
					}
				}
			}
		}
		return ip;
	}

}