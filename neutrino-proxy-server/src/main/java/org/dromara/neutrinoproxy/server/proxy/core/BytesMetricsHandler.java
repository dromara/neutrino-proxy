package org.dromara.neutrinoproxy.server.proxy.core;

import org.dromara.neutrinoproxy.server.proxy.domain.MetricsCollector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class BytesMetricsHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().localAddress();
        MetricsCollector metricsCollector = MetricsCollector.getCollector(sa.getPort());
        metricsCollector.incrementReadBytes(((ByteBuf) msg).readableBytes());
        metricsCollector.incrementReadMsgs(1);
//        System.out.println("字节数:" + metricsCollector.getMetrics().getReadBytes());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().localAddress();
        MetricsCollector metricsCollector = MetricsCollector.getCollector(sa.getPort());
        metricsCollector.incrementWriteBytes(((ByteBuf) msg).readableBytes());
        metricsCollector.incrementWroteMsgs(1);
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().localAddress();
        MetricsCollector.getCollector(sa.getPort()).getChannels().incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().localAddress();
        MetricsCollector.getCollector(sa.getPort()).getChannels().decrementAndGet();
        super.channelInactive(ctx);
    }

}
