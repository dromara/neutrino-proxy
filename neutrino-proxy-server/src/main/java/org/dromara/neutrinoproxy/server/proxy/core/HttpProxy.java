package org.dromara.neutrinoproxy.server.proxy.core;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyAttachment;
import org.dromara.neutrinoproxy.server.proxy.domain.VisitorChannelAttachInfo;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Component
public class HttpProxy implements EventListener<AppLoadEndEvent> {
    @Inject("serverBossGroup")
    private NioEventLoopGroup serverBossGroup;
    @Inject("serverWorkerGroup")
    private NioEventLoopGroup serverWorkerGroup;
    @Inject
    private ProxyConfig proxyConfig;
    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        if (StrUtil.isBlank(proxyConfig.getServer().getDomainName()) || null == proxyConfig.getServer().getHttpProxyPort()) {
            log.info("no config domain name,nonsupport http proxy.");
            return;
        }
        this.start();
    }

    private void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(serverBossGroup, serverWorkerGroup)
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addFirst(new BytesMetricsHandler());
                            ch.pipeline().addLast(new VisitorChannelHandler());
                        }
                    });
            bootstrap.bind("0.0.0.0", proxyConfig.getServer().getHttpProxyPort()).sync();
            log.info("Http代理服务启动成功！");
        } catch (Exception e) {
            log.error("http proxy start err!", e);
        }
    }

    private class VisitorChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
            if (StrUtil.isBlank(proxyConfig.getServer().getDomainName())) {
                ctx.channel().close();
                return;
            }

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

            String host = getHost(bytes);
            if (StringUtils.isBlank(host)) {
                ctx.channel().close();
                return;
            }
            log.debug("HttpProxy host: {}", host);
            if (!host.endsWith(proxyConfig.getServer().getDomainName())) {
                ctx.channel().close();
                return;
            }
            int index = host.lastIndexOf("." + proxyConfig.getServer().getDomainName());
            String subdomain = host.substring(0, index);

            // 根据域名拿到绑定的映射对应的cmdChannel
            Integer serverPort = ProxyUtil.getServerPortBySubdomain(subdomain);
            if (null == serverPort) {
                ctx.channel().close();
                return;
            }
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
            ProxyUtil.addVisitorChannelToCmdChannel(cmdChannel, visitorId, ctx.channel(), serverPort);
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
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // 当出现异常就关闭连接
            ctx.close();
        }

        private String getHost(byte[] buf) {
            String req = new String(buf);
            String[] lines = req.split("\r\n");
            String firstLine = lines[0];
            if (!(firstLine.endsWith("HTTP/1.1") || firstLine.endsWith("HTTP/1.0"))) {
                return null;
            }
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (!line.startsWith("Host: ")) {
                    continue;
                }
                // 域名
                String domain = line.substring(6);
                return domain;
            }
            return null;
        }
    }
}
