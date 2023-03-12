package fun.asgc.neutrino.proxy.server.proxy.handler;

import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
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
