package org.dromara.neutrinoproxy.server.proxy.security;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.util.HttpUtil;
import org.dromara.neutrinoproxy.core.util.IpUtil;
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
    /**
     * 域名
     */
    private String domainName;

    public HttpVisitorSecurityChannelHandler(String domainName) {
        this.domainName = domainName;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 未配置域名则不支持通过域名访问
        if (StrUtil.isBlank(domainName)) {
            ctx.channel().close();
            return;
        }

        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        // 获取Host请求头
        String httpContent = new String(bytes);
        String host = HttpUtil.getHostIgnorePort(httpContent);

        log.debug("HttpProxy host: {}", host);
        if (StringUtils.isBlank(host)) {
            ctx.channel().close();
            return;
        }

        // 根据Host匹配端口映射
        if (!host.endsWith(domainName)) {
            ctx.channel().close();
            return;
        }
        int index = host.lastIndexOf("." + domainName);
        String subdomain = host.substring(0, index);

        // 根据域名拿到绑定的映射对应的cmdChannel
        Integer serverPort = ProxyUtil.getServerPortBySubdomain(subdomain);
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

        // 继续传播
        ctx.channel().attr(Constants.SERVER_PORT).set(serverPort);
        buf.resetReaderIndex();
        ctx.fireChannelRead(buf);
    }

}
