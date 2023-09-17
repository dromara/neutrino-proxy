package org.dromara.neutrinoproxy.server.proxy.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.StandardCharsets;

/**
 * @author: aoshiguchen
 * @date: 2023/9/16
 */
public class UdpVisitorChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.out.println("服务端接收到消息 \nsender:" + datagramPacket.sender().toString() + "内容\n" + datagramPacket.content().toString(StandardCharsets.UTF_8));
    }

}
