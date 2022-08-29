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

import fun.asgc.neutrino.core.base.DefaultDispatcher;
import fun.asgc.neutrino.core.base.Dispatcher;
import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.proxy.core.*;
import fun.asgc.neutrino.proxy.server.util.ProxyChannelManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class ServerChannelHandler extends SimpleChannelInboundHandler<ProxyMessage> {
    private static volatile Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher;

    public ServerChannelHandler() {
        try {
            LockUtil.doubleCheckProcess(() -> null == dispatcher,
                ServerChannelHandler.class,
                () -> {
                    dispatcher = new DefaultDispatcher<>("消息调度器",
                        BeanManager.getBeanListBySuperClass(ProxyMessageHandler.class),
                        proxyMessage -> ProxyDataTypeEnum.of((int)proxyMessage.getType()) == null ? null : ProxyDataTypeEnum.of((int)proxyMessage.getType()).getName());
             });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyMessage proxyMessage) throws Exception {
        dispatcher.dispatch(ctx, proxyMessage);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel userChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (userChannel != null) {
            userChannel.config().setOption(ChannelOption.AUTO_READ, ctx.channel().isWritable());
        }

        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel userChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (userChannel != null && userChannel.isActive()) {
            String clientKey = ctx.channel().attr(Constants.CLIENT_KEY).get();
            String userId = ctx.channel().attr(Constants.USER_ID).get();
            Channel cmdChannel = ProxyChannelManager.getCmdChannel(clientKey);
            if (cmdChannel != null) {
                ProxyChannelManager.removeUserChannelFromCmdChannel(cmdChannel, userId);
            }

            // 数据发送完成后再关闭连接，解决http1.0数据传输问题
            userChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            userChannel.close();
        } else {
            ProxyChannelManager.removeCmdChannel(ctx.channel());
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
