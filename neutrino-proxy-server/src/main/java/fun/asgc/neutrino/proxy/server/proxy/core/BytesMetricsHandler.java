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

package fun.asgc.neutrino.proxy.server.proxy.core;

import fun.asgc.neutrino.proxy.server.proxy.domain.MetricsCollector;
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
        System.out.println("字节数:" + metricsCollector.getMetrics().getReadBytes());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().localAddress();
        MetricsCollector metricsCollector = MetricsCollector.getCollector(sa.getPort());
        metricsCollector.incrementWroteBytes(((ByteBuf) msg).readableBytes());
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
