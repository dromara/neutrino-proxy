package org.dromara.neutrinoproxy.server.proxy.security;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.util.IpUtil;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.noear.solon.Solon;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/12/14
 */
@Slf4j
public class TcpVisitorSecurityChannelHandler extends ChannelInboundHandlerAdapter {
    private final SecurityGroupService securityGroupService = Solon.context().getBean(SecurityGroupService.class);
    private final PortMappingService portMappingService = Solon.context().getBean(PortMappingService.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel visitorChannel = ctx.channel();

        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        // 判断IP是否在该端口绑定的安全组允许的规则内
        String ip = IpUtil.getRealRemoteIp(new String(bytes));
        if (StringUtils.isEmpty(ip)) {
            ip = IpUtil.getRemoteIp(ctx);
        }
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
        if (!securityGroupService.judgeAllow(ip, portMappingService.getSecurityGroupIdByMappingPort(sa.getPort()))) {
            // 不在安全组规则放行范围内
            ctx.channel().close();
            return;
        }
        // 继续传播
        buf.resetReaderIndex();
        ctx.fireChannelRead(buf);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel visitorChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();

        // 判断IP是否在该端口绑定的安全组允许的规则内
        if (!securityGroupService.judgeAllow(IpUtil.getRemoteIp(ctx), portMappingService.getSecurityGroupIdByMappingPort(sa.getPort()))) {
            // 不在安全组规则放行范围内
            ctx.channel().close();
            return;
        }

        // 继续传播
        ctx.fireChannelActive();
    }

}
