package org.dromara.neutrinoproxy.server.proxy.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.noear.solon.annotation.Component;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_TRANSFER)
@Component
public class UdpProxyMessageTransferHandler implements ProxyMessageHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final ProxyMessage.UdpBaseInfo udpBaseInfo = JSONObject.parseObject(proxyMessage.getInfo(), ProxyMessage.UdpBaseInfo.class);
        log.debug("[UDP transfer]info:{} data:{}", proxyMessage.getInfo(), new String(proxyMessage.getData()));

        Channel visitorChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (null != visitorChannel) {
            InetSocketAddress address = new InetSocketAddress(udpBaseInfo.getVisitorIp(), udpBaseInfo.getVisitorPort());
            ByteBuf byteBuf = Unpooled.copiedBuffer(proxyMessage.getData());
            visitorChannel.writeAndFlush(new DatagramPacket(byteBuf, address));
        }
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.UDP_TRANSFER.getDesc();
    }
}
