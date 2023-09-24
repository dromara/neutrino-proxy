package org.dromara.neutrinoproxy.server.proxy.handler;

import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.server.proxy.domain.VisitorChannelAttachInfo;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
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
			if (!visitorChannel.isWritable()) {
				//自己不可写，通道可以读，让通道关闭读
				//自己可写，通道不可以读，让通道打开读
				if (ctx.channel().config().isAutoRead()) {
					ctx.channel().config().setAutoRead(false);
				}
			} else {
				if (ctx.channel().config().isAutoRead()) {
					ctx.channel().config().setAutoRead(true);
				}
			}
			ByteBuf buf = ctx.alloc().buffer(proxyMessage.getData().length);
			buf.writeBytes(proxyMessage.getData());
			visitorChannel.writeAndFlush(buf);

			// 关闭http响应通道
//			ProxyUtil.closeHttpProxyResponseChannel(visitorChannel);

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
