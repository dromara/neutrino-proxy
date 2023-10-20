package org.dromara.neutrinoproxy.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.util.UdpServerUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Component;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/9/20
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_TRANSFER)
@Component
public class UdpProxyMessageTransferHandler implements ProxyMessageHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final ProxyMessage.UdpBaseInfo udpBaseInfo = ONode.deserialize(proxyMessage.getInfo(), ProxyMessage.UdpBaseInfo.class);
        log.debug("[UDP transfer]info:{} data:{}", proxyMessage.getInfo(), new String(proxyMessage.getData()));
        Channel channel = UdpServerUtil.takeChannel(udpBaseInfo, ctx.channel());
        if (null == channel) {
            log.error("[UDP transfer] take udp channel failed.");
            return;
        }
        log.debug("chid--->:{} port:{}", ctx.channel().id().asLongText(),  ((InetSocketAddress)channel.localAddress()).getPort());
        InetSocketAddress address = new InetSocketAddress(udpBaseInfo.getTargetIp(), udpBaseInfo.getTargetPort());
        ByteBuf byteBuf = Unpooled.copiedBuffer(proxyMessage.getData());
        channel.writeAndFlush(new DatagramPacket(byteBuf, address));
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.UDP_TRANSFER.getDesc();
    }
}
