/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.neutrinoproxy.server.proxy.core;

import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.dispatcher.Dispatcher;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.constant.ClientConnectTypeEnum;
import org.dromara.neutrinoproxy.server.constant.SuccessCodeEnum;
import org.dromara.neutrinoproxy.server.dal.entity.ClientConnectRecordDO;
import org.dromara.neutrinoproxy.server.proxy.domain.CmdChannelAttachInfo;
import org.dromara.neutrinoproxy.server.service.ClientConnectRecordService;
import org.dromara.neutrinoproxy.server.service.ProxyMutualService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class ProxyTunnelChannelHandler extends SimpleChannelInboundHandler<ProxyMessage> {
    private static volatile Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher;
    private static volatile Boolean transferLogEnable = Boolean.FALSE;

    public ProxyTunnelChannelHandler() {
        dispatcher = Solon.context().getBean(Dispatcher.class);
        ProxyConfig proxyConfig = Solon.context().getBean(ProxyConfig.class);
        if (null != proxyConfig.getTunnel() && null != proxyConfig.getTunnel().getHeartbeatLogEnable()) {
            transferLogEnable = proxyConfig.getTunnel().getHeartbeatLogEnable();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProxyMessage proxyMessage) throws Exception {
        if (ProxyMessage.TYPE_HEARTBEAT != proxyMessage.getType() || transferLogEnable) {
            log.debug("Server CmdChannel recieved proxy message, type is {}", proxyMessage.getType());
        }
        dispatcher.dispatch(ctx, proxyMessage);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel userChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (userChannel != null) {
            userChannel.config().setOption(ChannelOption.AUTO_READ, ctx.channel().isWritable());
        }

        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel visitorChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (null != visitorChannel) {
            Integer licenseId = ctx.channel().attr(Constants.LICENSE_ID).get();
            String visitorId = ctx.channel().attr(Constants.VISITOR_ID).get();
            Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(licenseId);

            if (null != cmdChannel) {
                ProxyUtil.removeVisitorChannelFromCmdChannel(cmdChannel, visitorId);
            }
            ProxyUtil.remoteProxyConnectAttachment(visitorId);

            // 此处如果时UDP的 visitorChannel，则不能close，先临时判断一下
            Boolean isUdp = visitorChannel.attr(Constants.IS_UDP_KEY).get();
            if (visitorChannel.isActive() && null == isUdp) {
                // 数据发送完成后再关闭连接，解决http1.0数据传输问题
                visitorChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                visitorChannel.close();
            }
        } else {
            CmdChannelAttachInfo cmdChannelAttachInfo = ProxyUtil.getAttachInfo(ctx.channel());
            if (null != cmdChannelAttachInfo) {
                Channel curCmdChannel = ProxyUtil.getCmdChannelByLicenseId(cmdChannelAttachInfo.getLicenseId());
                // 客户端切换网络后，连接断开，但服务端还未触发断开事件。此时客户端重连上了，然后服务端触发了断开，此时不应该更新在线状态
                if (curCmdChannel == ctx.channel()) {
                    Solon.context().getBean(ProxyMutualService.class).offline(cmdChannelAttachInfo);
                    ProxyUtil.removeCmdChannel(ctx.channel());
                    // 防止下次换一个客户端，无法连接的情况
                    ProxyUtil.removeClientIdByLicenseId(cmdChannelAttachInfo.getLicenseId());
                }
                // 即便是因为上述原因断开，断开的日志依然要记录，方便排查问题
                Solon.context().getBean(ClientConnectRecordService.class).add(new ClientConnectRecordDO()
                        .setIp(((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress())
                        .setLicenseId(cmdChannelAttachInfo.getLicenseId())
                        .setType(ClientConnectTypeEnum.DISCONNECT.getType())
                        .setMsg("")
                        .setCode(SuccessCodeEnum.SUCCESS.getCode())
                        .setCreateTime(new Date())
                );
            }
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        if (ctx.channel().isActive()) {
            ctx.channel().close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            switch (event.state()) {
                case READER_IDLE:
                    // 读超时，断开连接
                    log.debug("Read timeout");
                    ctx.channel().close();
                    break;
                case WRITER_IDLE:
                    ctx.channel().writeAndFlush(ProxyMessage.buildHeartbeatMessage());
                    break;
                case ALL_IDLE:
                    break;
            }
        }
    }
}
