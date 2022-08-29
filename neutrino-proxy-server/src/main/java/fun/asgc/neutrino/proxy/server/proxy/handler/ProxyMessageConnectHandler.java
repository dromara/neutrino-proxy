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

package fun.asgc.neutrino.proxy.server.proxy.handler;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Match;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.server.util.ProxyChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@NonIntercept
@Match(type = Constants.ProxyDataTypeName.CONNECT)
@Component
public class ProxyMessageConnectHandler implements ProxyMessageHandler {

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		String info = proxyMessage.getInfo();
		if (info == null) {
			ctx.channel().close();
			return;
		}

		String[] tokens = info.split("@");
		if (tokens.length != 2) {
			ctx.channel().close();
			return;
		}

		Channel cmdChannel = ProxyChannelManager.getCmdChannel(tokens[1]);
		if (cmdChannel == null) {
			ctx.channel().close();
			return;
		}

		Channel userChannel = ProxyChannelManager.getUserChannel(cmdChannel, tokens[0]);
		if (userChannel != null) {
			ctx.channel().attr(Constants.USER_ID).set(tokens[0]);
			ctx.channel().attr(Constants.CLIENT_KEY).set(tokens[1]);
			ctx.channel().attr(Constants.NEXT_CHANNEL).set(userChannel);
			userChannel.attr(Constants.NEXT_CHANNEL).set(ctx.channel());
			// 代理客户端与后端服务器连接成功，修改用户连接为可读状态
			userChannel.config().setOption(ChannelOption.AUTO_READ, true);
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.CONNECT.getDesc();
	}
}
