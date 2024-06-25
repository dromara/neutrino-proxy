package org.dromara.neutrinoproxy.server.proxy.enhance;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyAttachment;
import org.dromara.neutrinoproxy.server.proxy.domain.VisitorChannelAttachInfo;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.Solon;

import java.net.InetSocketAddress;

/**
 * HTTP 访客处理器
 * @author: aoshiguchen
 * @date: 2023/5/27
 */
@Slf4j
public class HttpVisitorChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        byteBuf.resetReaderIndex();

        ProxyAttachment proxyAttachment = new ProxyAttachment(ctx.channel(), bytes, (channel, buf) -> {
            Channel proxyChannel = channel.attr(Constants.NEXT_CHANNEL).get();
            if (null == proxyChannel) {
                // 该端口还没有代理客户端
                ctx.channel().close();
                return;
            }
            proxyChannel.writeAndFlush(ProxyMessage.buildTransferMessage(ProxyUtil.getVisitorIdByChannel(channel), bytes));
            // 增加流量计数
            VisitorChannelAttachInfo visitorChannelAttachInfo = ProxyUtil.getAttachInfo(channel);
            Solon.context().getBean(FlowReportService.class).addWriteByte(visitorChannelAttachInfo.getLicenseId(), bytes.length);
        });
        String visitorId = ProxyUtil.getVisitorIdByChannel(ctx.channel());
        if (StringUtils.isNotBlank(visitorId)) {
            proxyAttachment.execute();
            return;
        }

        // 用户连接到代理服务器时，设置用户连接不可读，等待代理后端服务器连接成功后再改变为可读状态
        ctx.channel().config().setOption(ChannelOption.AUTO_READ, false);
        // 根据域名拿到绑定的映射对应的cmdChannel
        Integer serverPort = ctx.channel().attr(Constants.SERVER_PORT).get();
        Channel cmdChannel = ProxyUtil.getCmdChannelByServerPort(serverPort);

        if (null == cmdChannel) {
            ctx.channel().close();
            return;
        }
        String lanInfo = ProxyUtil.getClientLanInfoByServerPort(serverPort);
        if (StringUtils.isBlank(lanInfo)) {
            ctx.channel().close();
            return;
        }
        visitorId = ProxyUtil.newVisitorId();
        ProxyUtil.addVisitorChannelToCmdChannel(NetworkProtocolEnum.HTTP, cmdChannel, visitorId, ctx.channel(), serverPort);
        ProxyUtil.addProxyConnectAttachment(visitorId, proxyAttachment);
        cmdChannel.writeAndFlush(ProxyMessage.buildConnectMessage(visitorId).setData(lanInfo.getBytes()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通知代理客户端
        Channel visitorChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) visitorChannel.localAddress();
        Channel cmdChannel = ProxyUtil.getCmdChannelByServerPort(sa.getPort());

        if (cmdChannel == null) {
            // 该端口还没有代理客户端
            ctx.channel().close();
        } else {
            // 用户连接断开，从控制连接中移除
            String visitorId = ProxyUtil.getVisitorIdByChannel(visitorChannel);
            ProxyUtil.removeVisitorChannelFromCmdChannel(cmdChannel, visitorId);

            // 删除代理附加对象
            ProxyUtil.remoteProxyConnectAttachment(visitorId);

            Channel proxyChannel = visitorChannel.attr(Constants.NEXT_CHANNEL).get();
            if (proxyChannel != null && proxyChannel.isActive()) {
                proxyChannel.attr(Constants.NEXT_CHANNEL).remove();
                proxyChannel.attr(Constants.LICENSE_ID).remove();
                proxyChannel.attr(Constants.VISITOR_ID).remove();

                proxyChannel.config().setOption(ChannelOption.AUTO_READ, true);
                // 通知客户端，用户连接已经断开
                proxyChannel.writeAndFlush(ProxyMessage.buildDisconnectMessage(visitorId));
            }
        }

        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        ctx.close();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

        // 通知代理客户端
        Channel visitorChannel = ctx.channel();
        Channel proxyChannel = visitorChannel.attr(Constants.NEXT_CHANNEL).get();
        if (null != proxyChannel) {
            proxyChannel.config().setOption(ChannelOption.AUTO_READ, visitorChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }
}
