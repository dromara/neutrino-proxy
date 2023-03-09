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

import fun.asgc.neutrino.core.annotation.Match;
import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.server.proxy.domain.VisitorChannelAttachInfo;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import fun.asgc.neutrino.proxy.server.util.ProxyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Match(type = Constants.ProxyDataTypeName.TRANSFER)
@Component
public class ProxyMessageTransferHandler implements ProxyMessageHandler {

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		Channel visitorChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
		if (null != visitorChannel) {
			ByteBuf buf = ctx.alloc().buffer(proxyMessage.getData().length);
			buf.writeBytes(proxyMessage.getData());
			visitorChannel.writeAndFlush(buf);

			// 增加流量计数
			VisitorChannelAttachInfo visitorChannelAttachInfo = ProxyUtil.getAttachInfo(visitorChannel);
			Solon.context().getBean(FlowReportService.class).addReadByte(visitorChannelAttachInfo.getLicenseId(), proxyMessage.getData().length);
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.TRANSFER.getDesc();
	}

}
