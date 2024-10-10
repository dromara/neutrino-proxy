package org.dromara.neutrinoproxy.server.proxy.security;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.util.HttpUtil;
import org.dromara.neutrinoproxy.core.util.IpUtil;
import org.dromara.neutrinoproxy.server.service.DomainService;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.Solon;

/**
 * @author: aoshiguchen
 * @date: 2023/12/14
 */
@Slf4j
public class HttpVisitorSecurityChannelHandler extends ChannelInboundHandlerAdapter {
    private final SecurityGroupService securityGroupService = Solon.context().getBean(SecurityGroupService.class);
    private final PortMappingService portMappingService = Solon.context().getBean(PortMappingService.class);
    private final DomainService domainService = Solon.context().getBean(DomainService.class);
    /**
     * 域名
     */
    private Boolean isHttps;

    public HttpVisitorSecurityChannelHandler(Boolean isHttps) {
        this.isHttps = isHttps;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        // 获取Host请求头
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String httpContent = new String(bytes);
        String host = HttpUtil.getHostIgnorePort(httpContent); //test1.asgc.fun

        log.debug("HttpProxy host: {}", host);
        if (StringUtils.isBlank(host)) {
            ctx.channel().close();
            return;
        }
        // 判断域名是否被禁用或删除
        Integer domainNameId = ProxyUtil.getDomainNameIdByFullDomain(host);
        if (domainNameId == null) {
            ctx.channel().close();
            return;
        }
        // 域名映射强制https验证
        if (!isHttps && domainService.isOnlyHttps(domainNameId)) {
            ctx.channel().close();
            return;
        }

        Integer serverPort = ctx.channel().attr(Constants.SERVER_PORT).get();
        if (null == serverPort) {
            // channel没有服务器端口信息，尝试根据完整域名拿到服务端端口
            serverPort = ProxyUtil.getServerPortByFullDomain(host);
            if (null == serverPort) {
                ctx.channel().close();
                return;
            }

            // 判断IP是否在该端口绑定的安全组允许的规则内
            String ip = IpUtil.getRealRemoteIp(httpContent);
            if (ip == null) {
                ip = IpUtil.getRemoteIp(ctx);
            }
            if (!securityGroupService.judgeAllow(ip, portMappingService.getSecurityGroupIdByMappingPort(serverPort))) {
                // 不在安全组规则放行范围内
                ctx.channel().close();
                return;
            }

            ctx.channel().attr(Constants.REAL_REMOTE_IP).set(ip);
            ctx.channel().attr(Constants.SERVER_PORT).set(serverPort);
        }

        // 继续传播
        buf.resetReaderIndex();
        ctx.fireChannelRead(buf);
    }

}
