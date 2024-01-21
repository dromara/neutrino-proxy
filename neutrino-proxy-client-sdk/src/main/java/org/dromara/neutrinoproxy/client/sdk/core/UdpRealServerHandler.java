package org.dromara.neutrinoproxy.client.sdk.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.constant.Constants;
import org.dromara.neutrinoproxy.client.sdk.util.UdpChannelBindInfo;
import org.dromara.neutrinoproxy.core.ProxyMessage;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
@Slf4j
public class UdpRealServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        log.debug("chid---<:{} port:{}", ctx.channel().id().asLongText(), ((InetSocketAddress)ctx.channel().localAddress()).getPort());
        UdpChannelBindInfo udpChannelBindInfo = ctx.channel().attr(Constants.UDP_CHANNEL_BIND_KEY).get();
        if (null != udpChannelBindInfo) {
            byte[] bytes = new byte[datagramPacket.content().readableBytes()];
            datagramPacket.content().readBytes(bytes);

            udpChannelBindInfo.getTunnelChannel().writeAndFlush(ProxyMessage.buildUdpTransferMessage(new ProxyMessage.UdpBaseInfo()
                            .setVisitorId(udpChannelBindInfo.getVisitorId())
                            .setVisitorIp(udpChannelBindInfo.getVisitorIp())
                            .setVisitorPort(udpChannelBindInfo.getVisitorPort())
                            .setServerPort(udpChannelBindInfo.getServerPort())
                            .setTargetIp(udpChannelBindInfo.getTargetIp())
                            .setTargetPort(udpChannelBindInfo.getTargetPort()))
                    .setData(bytes)
            );

            udpChannelBindInfo.getLockChannel().setResponseCount(udpChannelBindInfo.getLockChannel().getResponseCount() + 1);
        }
    }
}
