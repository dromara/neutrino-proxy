package org.dromara.neutrinoproxy.client.sdk.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;

/**
 * 传输信息处理器
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Match(type = Constants.ProxyDataTypeName.TRANSFER)
@Slf4j
public class ProxyMessageTransferHandler implements ProxyMessageHandler {

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		Channel realServerChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
		if (realServerChannel != null) {

			// 自己可写，则设置来源可读。自己不可写，则设置来源不可读
			ctx.channel().config().setAutoRead(realServerChannel.isWritable());

			ByteBuf buf = ctx.alloc().buffer(proxyMessage.getData().length);
			buf.writeBytes(proxyMessage.getData());
			realServerChannel.writeAndFlush(buf);
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.TRANSFER.getDesc();
	}

}
