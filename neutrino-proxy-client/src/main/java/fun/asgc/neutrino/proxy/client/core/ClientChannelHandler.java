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

package fun.asgc.neutrino.proxy.client.core;

import fun.asgc.neutrino.core.base.DefaultDispatcher;
import fun.asgc.neutrino.core.base.Dispatcher;
import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.proxy.client.util.ClientChannelMannager;
import fun.asgc.neutrino.proxy.core.*;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理与服务端之间的数据传输
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class ClientChannelHandler extends SimpleChannelInboundHandler<ProxyMessage> {

    private static volatile Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher;


    public ClientChannelHandler() {
        LockUtil.doubleCheckProcess(() -> null == dispatcher,
            ClientChannelHandler.class,
            () -> {
                dispatcher = new DefaultDispatcher<>("消息调度器",
                    BeanManager.getBeanListBySuperClass(ProxyMessageHandler.class),
                    proxyMessage -> ProxyDataTypeEnum.of((int)proxyMessage.getType()) == null ? null : ProxyDataTypeEnum.of((int)proxyMessage.getType()).getName());
            });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyMessage proxyMessage) throws Exception {
        log.info("recieved proxy message, type is {}", proxyMessage.getType());
        dispatcher.dispatch(ctx, proxyMessage);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel realServerChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (realServerChannel != null) {
            realServerChannel.config().setOption(ChannelOption.AUTO_READ, ctx.channel().isWritable());
        }

        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 控制连接
        if (ClientChannelMannager.getCmdChannel() == ctx.channel()) {
            log.info("与服务端断开连接");
            ClientChannelMannager.setCmdChannel(null);
            ClientChannelMannager.clearRealServerChannels();
        } else {
            // 数据传输连接
            Channel realServerChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
            if (realServerChannel != null && realServerChannel.isActive()) {
                realServerChannel.close();
            }
        }

        ClientChannelMannager.removeProxyChanel(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

}
