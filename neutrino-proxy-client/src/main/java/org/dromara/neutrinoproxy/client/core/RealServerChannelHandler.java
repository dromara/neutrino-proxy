package org.dromara.neutrinoproxy.client.core;

import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理与被代理客户端的数据传输
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class RealServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        Channel realServerChannel = ctx.channel();
        Channel proxyChannel = realServerChannel.attr(Constants.NEXT_CHANNEL).get();
        if (null == proxyChannel) {
            // 代理客户端连接断开
            ctx.channel().close();
        } else {

            if (proxyChannel.isWritable()) {
                if (!realServerChannel.config().isAutoRead()) {
                    realServerChannel.config().setAutoRead(true);
                }
            } else {
                if (realServerChannel.config().isAutoRead()) {
                    realServerChannel.config().setAutoRead(false);
                }
            }

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String visitorId = ProxyUtil.getVisitorIdByRealServerChannel(realServerChannel);
            proxyChannel.writeAndFlush(ProxyMessage.buildTransferMessage(visitorId, bytes));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel realServerChannel = ctx.channel();
        String visitorId = ProxyUtil.getVisitorIdByRealServerChannel(realServerChannel);
        ProxyUtil.removeRealServerChannel(visitorId);
        Channel channel = realServerChannel.attr(Constants.NEXT_CHANNEL).get();
        if (channel != null) {
            channel.writeAndFlush(ProxyMessage.buildDisconnectMessage(visitorId));
        }

        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel realServerChannel = ctx.channel();
        Channel proxyChannel = realServerChannel.attr(Constants.NEXT_CHANNEL).get();
        if (proxyChannel != null) {
            proxyChannel.config().setOption(ChannelOption.AUTO_READ, realServerChannel.isWritable());
        }

        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client ProxyChannel Error", cause);
    }
}
