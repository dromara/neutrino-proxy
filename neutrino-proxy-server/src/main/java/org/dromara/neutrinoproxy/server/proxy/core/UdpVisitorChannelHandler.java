package org.dromara.neutrinoproxy.server.proxy.core;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author: aoshiguchen
 * @date: 2023/9/16
 */
public class UdpVisitorChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        System.out.println("服务端接收到消息 \nsender:" + datagramPacket.sender().toString() + "内容\n" + datagramPacket.content().toString(StandardCharsets.UTF_8));

        Channel visitorChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
        Channel cmdChannel = ProxyUtil.getCmdChannelByServerPort(sa.getPort());

        if (null == cmdChannel) {
            // 该端口还没有代理客户端
            ctx.channel().close();
            return;
        }

        // 根据代理服务端端口，获取被代理客户端局域网连接信息
        String lanInfo = ProxyUtil.getClientLanInfoByServerPort(sa.getPort());
        if (StrUtil.isEmpty(lanInfo)) {
            ctx.channel().close();
            return;
        }

        String[] targetInfo = lanInfo.split(":");
        String targetIp = targetInfo[0];
        int targetPort = Integer.parseInt(targetInfo[1]);

        // 转发代理数据
        byte[] bytes = new byte[datagramPacket.content().readableBytes()];
        datagramPacket.content().readBytes(bytes);

        cmdChannel.writeAndFlush(ProxyMessage.buildUdpTransferMessage(
                sa.getAddress().getHostAddress(),
                sa.getPort(),
                targetIp,
                targetPort,
                bytes
        ));
    }
}
