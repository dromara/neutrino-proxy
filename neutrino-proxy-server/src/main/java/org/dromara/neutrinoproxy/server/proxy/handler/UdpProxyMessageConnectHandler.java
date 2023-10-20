package org.dromara.neutrinoproxy.server.proxy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ExceptionEnum;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyAttachment;
import org.dromara.neutrinoproxy.server.service.LicenseService;
import org.dromara.neutrinoproxy.server.service.UserService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author: aoshiguchen
 * @date: 2023/9/19
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_CONNECT)
@Component
public class UdpProxyMessageConnectHandler implements ProxyMessageHandler {
    @Inject
    private LicenseService licenseService;
    @Inject
    private UserService userService;
    @Inject
    private PortMappingMapper portMappingMapper;

    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final ProxyMessage.UdpBaseInfo udpBaseInfo = ONode.deserialize(proxyMessage.getInfo(), ProxyMessage.UdpBaseInfo.class);
        final String licenseKey = new String(proxyMessage.getData());
        log.info("[UDP connect]info:{} licenseKey:{}", proxyMessage.getInfo(), licenseKey);

        LicenseDO licenseDO = licenseService.findByKey(licenseKey);
        if (null == licenseDO) {
            ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "the license notfound!"));
            ctx.channel().close();
            return;
        }
        if (EnableStatusEnum.DISABLE.getStatus().equals(licenseDO.getEnable())) {
            ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "the license disabled!"));
            ctx.channel().close();
            return;
        }
        UserDO userDO = userService.findById(licenseDO.getUserId());
        if (null == userDO || EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
            ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "the license invalid!"));
            ctx.channel().close();
            return;
        }

        Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(licenseDO.getId());

        if (null == cmdChannel) {
            ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "server error，cmd channel notfound!"));
            ctx.channel().close();
            return;
        }

        Channel visitorChannel = ProxyUtil.getVisitorChannel(cmdChannel, udpBaseInfo.getVisitorId());
        if (null == visitorChannel) {
            return;
        }
        PortMappingDO portMappingDO = portMappingMapper.findByLicenseIdAndServerPort(licenseDO.getId(), udpBaseInfo.getServerPort());
        if (null == portMappingDO || !EnableStatusEnum.ENABLE.getStatus().equals(portMappingDO.getEnable())) {
            ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "server error, port mapping notfound!"));
            ctx.channel().close();
            return;
        }

        ctx.channel().attr(Constants.VISITOR_ID).set(udpBaseInfo.getVisitorId());
        ctx.channel().attr(Constants.LICENSE_ID).set(licenseDO.getId());
        ctx.channel().attr(Constants.NEXT_CHANNEL).set(visitorChannel);
        ctx.channel().attr(Constants.TARGET_IP).set(portMappingDO.getClientIp());
        ctx.channel().attr(Constants.TARGET_PORT).set(portMappingDO.getClientPort());
        ctx.channel().attr(Constants.PROXY_RESPONSES).set(portMappingDO.getProxyResponses());
        ctx.channel().attr(Constants.PROXY_TIMEOUT_MS).set(portMappingDO.getProxyTimeoutMs());
        visitorChannel.attr(Constants.NEXT_CHANNEL).set(ctx.channel());
        visitorChannel.attr(Constants.IS_UDP_KEY).set(Boolean.TRUE);
//        // 代理客户端与后端服务器连接成功，修改用户连接为可读状态
//        visitorChannel.config().setOption(ChannelOption.AUTO_READ, true);
        // 获取代理附加对象
        ProxyAttachment proxyAttachment = ProxyUtil.getProxyConnectAttachment(udpBaseInfo.getVisitorId());
        if (null != proxyAttachment) {
            // 及时释放
            ProxyUtil.remoteProxyConnectAttachment(udpBaseInfo.getVisitorId());
            proxyAttachment.execute();
        }
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.UDP_CONNECT.getDesc();
    }
}
