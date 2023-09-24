package org.dromara.neutrinoproxy.server.proxy.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.server.proxy.domain.VisitorChannelAttachInfo;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;

import java.net.InetSocketAddress;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_TRANSFER)
@Component
public class UdpProxyMessageTransferHandler implements ProxyMessageHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final ProxyMessage.UdpBaseInfo udpBaseInfo = JSONObject.parseObject(proxyMessage.getInfo(), ProxyMessage.UdpBaseInfo.class);
        log.debug("[UDP transfer]info:{} data:{}", proxyMessage.getInfo(), new String(proxyMessage.getData()));

        Channel visitorChannel = ctx.channel().attr(Constants.NEXT_CHANNEL).get();
        if (null != visitorChannel) {

            if (!visitorChannel.isWritable()) {
                //自己不可写，通道可以读，让通道关闭读
                //自己可写，通道不可以读，让通道打开读
                if (ctx.channel().config().isAutoRead()) {
                    ctx.channel().config().setAutoRead(false);
                }
            } else {
                if (ctx.channel().config().isAutoRead()) {
                    ctx.channel().config().setAutoRead(true);
                }
            }

//            InetSocketAddress address = new InetSocketAddress(udpBaseInfo.getVisitorIp(), udpBaseInfo.getVisitorPort());
//            ByteBuf byteBuf = Unpooled.copiedBuffer(proxyMessage.getData());
//            visitorChannel.writeAndFlush(new DatagramPacket(byteBuf, address));
            InetSocketAddress address = ctx.channel().attr(Constants.SENDER).get();
            if (null != address) {
                visitorChannel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(proxyMessage.getData()), address));
            }

            // 增加流量计数(TODO 如果UDP映射服务端端口修改，这个似乎不准)
            Integer licenseId = visitorChannel.attr(Constants.LICENSE_ID).get();
            if (null != licenseId) {
                Solon.context().getBean(FlowReportService.class).addReadByte(licenseId, proxyMessage.getData().length);
            }
        }
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.UDP_TRANSFER.getDesc();
    }
}
