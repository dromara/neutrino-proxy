package org.dromara.neutrinoproxy.server.proxy.core;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.util.IpUtil;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyAttachment;
import org.dromara.neutrinoproxy.server.proxy.domain.VisitorChannelAttachInfo;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author: aoshiguchen
 * @date: 2023/9/16
 */
@Slf4j
public class UdpVisitorChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final SecurityGroupService securityGroupService = Solon.context().getBean(SecurityGroupService.class);

    private final PortMappingService portMappingService = Solon.context().getBean(PortMappingService.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        log.debug("chid>>>{}", ctx.channel().id().asLongText());
        Channel visitorChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();

        // 判断IP是否在该端口绑定的安全组允许的规则内
        if (!securityGroupService.judgeAllow(IpUtil.getRemoteIp(ctx), portMappingService.getSecurityGroupIdByMappingPort(sa.getPort()))) {
            // 不在安全组规则放行范围内
            ctx.channel().close();
            return;
        }

        byte[] bytes = new byte[datagramPacket.content().readableBytes()];
        datagramPacket.content().readBytes(bytes);
        datagramPacket.content().resetReaderIndex();
        ProxyAttachment proxyAttachment = new ProxyAttachment(ctx.channel(), bytes, (channel, buf) -> {
            Channel proxyChannel = channel.attr(Constants.NEXT_CHANNEL).get();

            if (null == proxyChannel) {
//                // 该端口还没有代理客户端
//                ctx.channel().close();
                return;
            }

            proxyChannel.attr(Constants.SENDER).set(datagramPacket.sender());
            String targetIp = proxyChannel.attr(Constants.TARGET_IP).get();
            int targetPort = proxyChannel.attr(Constants.TARGET_PORT).get();
            Integer proxyResponses = proxyChannel.attr(Constants.PROXY_RESPONSES).get();
            Long proxyTimeoutMs = proxyChannel.attr(Constants.PROXY_TIMEOUT_MS).get();

            // 转发代理数据
            String visitorId = ProxyUtil.getVisitorIdByChannel(channel);
            proxyChannel.writeAndFlush(ProxyMessage.buildUdpTransferMessage(new ProxyMessage.UdpBaseInfo()
                    .setVisitorId(visitorId)
                    .setVisitorIp(datagramPacket.sender().getAddress().getHostAddress())
                    .setVisitorPort(datagramPacket.sender().getPort())
                    .setTargetIp(targetIp)
                    .setTargetPort(targetPort)
                    .setProxyTimeoutMs(proxyTimeoutMs)
                    .setProxyResponses(proxyResponses)
            ).setData(bytes));

            // 增加流量计数
            VisitorChannelAttachInfo visitorChannelAttachInfo = ProxyUtil.getAttachInfo(channel);
            Solon.context().getBean(FlowReportService.class).addWriteByte(visitorChannelAttachInfo.getLicenseId(), bytes.length);
        });

//        String visitorId = ProxyUtil.getVisitorIdByChannel(ctx.channel());
//        if (StringUtils.isNotBlank(visitorId)) {
//            // UDP代理隧道已就绪，直接转发
//            proxyAttachment.execute();
//            return;
//        }
        Channel proxyChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (null != proxyChannel && proxyChannel.isActive()) {
            // UDP代理隧道已就绪，直接转发
            proxyAttachment.execute();
            return;
        }

        Channel cmdChannel = ProxyUtil.getCmdChannelByServerPort(sa.getPort());

        // 没有指令通道，直接结束
        if (null == cmdChannel) {
            // 该端口还没有代理客户端
//            ctx.channel().close();
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

//        // 用户连接到代理服务器时，设置用户连接不可读，等待代理后端服务器连接成功后再改变为可读状态
//        visitorChannel.config().setOption(ChannelOption.AUTO_READ, false);

        // TODO UDP此处叫visitor似有不妥，与TCP不同,2.x重构思考
        String visitorId = ProxyUtil.newVisitorId();
        // 此处需要和tcp分开
        ProxyUtil.addVisitorChannelToCmdChannel(NetworkProtocolEnum.UDP, cmdChannel, visitorId, visitorChannel, sa.getPort());
        ProxyUtil.addProxyConnectAttachment(visitorId, proxyAttachment);
        cmdChannel.writeAndFlush(ProxyMessage.buildUdpConnectMessage(new ProxyMessage.UdpBaseInfo()
                .setVisitorId(visitorId)
                .setServerPort(sa.getPort())
                .setTargetIp(targetIp)
                .setTargetPort(targetPort)
        ));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 判断IP是否在该端口绑定的安全组允许的规则内
        InetSocketAddress sa = (InetSocketAddress) ctx.channel().localAddress();
        if (!securityGroupService.judgeAllow(IpUtil.getRemoteIp(ctx), portMappingService.getSecurityGroupIdByMappingPort(sa.getPort()))) {
            // 不在安全组规则放行范围内
            ctx.channel().close();
            return;
        }
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        ctx.close();
        log.error("[UDP Visitor Channel]VisitorChannel error", cause);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

        // 通知代理客户端
        Channel visitorChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
        Channel cmdChannel = ProxyUtil.getCmdChannelByServerPort(sa.getPort());

        if (null == cmdChannel) {
            // 该端口还没有代理客户端
            ctx.channel().close();
        }
        else {
            Channel proxyChannel = visitorChannel.attr(Constants.NEXT_CHANNEL).get();
            if (null != proxyChannel) {
                proxyChannel.config().setOption(ChannelOption.AUTO_READ, visitorChannel.isWritable());
            }
        }

        super.channelWritabilityChanged(ctx);
    }
}
