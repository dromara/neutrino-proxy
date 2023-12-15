package org.dromara.neutrinoproxy.server.proxy.security;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.noear.solon.Solon;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/12/14
 */
@Slf4j
public class UdpVisitorSecurityChannelHandler extends ChannelInboundHandlerAdapter {
    private final SecurityGroupService securityGroupService = Solon.context().getBean(SecurityGroupService.class);
    private final PortMappingService portMappingService = Solon.context().getBean(PortMappingService.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel visitorChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
        DatagramPacket datagramPacket = (DatagramPacket) msg;

        // 判断IP是否在该端口绑定的安全组允许的规则内
        if (!securityGroupService.judgeAllow(datagramPacket.sender().getAddress().getHostAddress(), portMappingService.getSecurityGroupIdByMappingPort(sa.getPort()))) {
            return;
        }

        // 继续传播
        ctx.channel().attr(Constants.SERVER_PORT).set(sa.getPort());
        ctx.fireChannelRead(msg);
    }
}
