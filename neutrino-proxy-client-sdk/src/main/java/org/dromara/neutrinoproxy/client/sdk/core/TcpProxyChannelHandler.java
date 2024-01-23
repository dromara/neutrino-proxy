package org.dromara.neutrinoproxy.client.sdk.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.handler.ProxyMessageFactory;
import org.dromara.neutrinoproxy.client.sdk.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;

/**
 * 处理与服务端之间的数据传输
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class TcpProxyChannelHandler extends SimpleChannelInboundHandler<ProxyMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyMessage proxyMessage) throws Exception {
        if (ProxyMessage.TYPE_HEARTBEAT != proxyMessage.getType()) {
            log.debug("[TCP Proxy Channel]Client ProxyChannel recieved proxy message, type is {}", proxyMessage.getType());
        }
        Dispatcher dispatcher = (Dispatcher) ProxyMessageFactory.beanManager.get("dispatcher").getBean();
        dispatcher.dispatch(ctx,proxyMessage);
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
        // 数据传输连接
        Channel realServerChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (realServerChannel != null && realServerChannel.isActive()) {
            realServerChannel.close();
        }

        ProxyUtil.removeTcpProxyChanel(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
        log.error("[TCP Proxy Channel]Client ProxyChannel Error channelId:{}", ctx.channel().id().asLongText(), cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)  {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            switch (event.state()) {
                case READER_IDLE:
                    if (ctx.channel().isWritable()) {
                        // 读超时，断开连接
                        log.info("[TCP Proxy Channel]Read timeout");
                        ctx.channel().close();
                    }
                    break;
                case WRITER_IDLE:
                    ctx.channel().writeAndFlush(ProxyMessage.buildHeartbeatMessage());
                    break;
                case ALL_IDLE:
//                    log.debug("[TCP Proxy Channel]ReadWrite timeout");
//                    ctx.close();
                    break;
            }
        }
    }
}
