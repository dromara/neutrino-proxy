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

package fun.asgc.neutrino.proxy.client.handler;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Match;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import fun.asgc.neutrino.proxy.client.core.ProxyChannelBorrowListener;
import fun.asgc.neutrino.proxy.client.util.ProxyUtil;
import fun.asgc.neutrino.proxy.core.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@NonIntercept
@Match(type = Constants.ProxyDataTypeName.CONNECT)
@Component
public class ProxyMessageConnectHandler implements ProxyMessageHandler {
	@Autowired("bootstrap")
	private Bootstrap bootstrap;
	@Autowired("realServerBootstrap")
	private Bootstrap realServerBootstrap;

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		final Channel cmdChannel = ctx.channel();
		final String userId = proxyMessage.getInfo();
		String[] serverInfo = new String(proxyMessage.getData()).split(":");
		String ip = serverInfo[0];
		int port = Integer.parseInt(serverInfo[1]);
		realServerBootstrap.connect(ip, port).addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				// 连接后端服务器成功
				if (future.isSuccess()) {
					final Channel realServerChannel = future.channel();

					realServerChannel.config().setOption(ChannelOption.AUTO_READ, false);

					// 获取连接
					ProxyUtil.borrowProxyChanel(bootstrap, new ProxyChannelBorrowListener() {

						@Override
						public void success(Channel channel) {
							// 连接绑定
							channel.attr(Constants.NEXT_CHANNEL).set(realServerChannel);
							realServerChannel.attr(Constants.NEXT_CHANNEL).set(channel);

							// 远程绑定
							channel.writeAndFlush(ProxyMessage.buildConnectMessage(userId + "@" + ProxyConfig.instance.getLicenseKey()));

							realServerChannel.config().setOption(ChannelOption.AUTO_READ, true);
							ProxyUtil.addRealServerChannel(userId, realServerChannel);
							ProxyUtil.setRealServerChannelUserId(realServerChannel, userId);
						}

						@Override
						public void error(Throwable cause) {
							ProxyMessage proxyMessage = new ProxyMessage();
							proxyMessage.setType(ProxyMessage.TYPE_DISCONNECT);
							proxyMessage.setInfo(userId);
							cmdChannel.writeAndFlush(proxyMessage);
						}
					});

				} else {
					cmdChannel.writeAndFlush(ProxyMessage.buildDisconnectMessage(userId));
				}
			}
		});
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.CONNECT.getDesc();
	}
}
