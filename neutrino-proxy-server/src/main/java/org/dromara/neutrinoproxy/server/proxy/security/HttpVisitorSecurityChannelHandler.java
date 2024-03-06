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
import org.dromara.neutrinoproxy.server.proxy.domain.DomainMapping;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.Solon;

/**
 * HTTP 访问安全检测
 * @author: aoshiguchen
 * @date: 2023/12/14
 */
@Slf4j
public class HttpVisitorSecurityChannelHandler extends ChannelInboundHandlerAdapter {
    private final SecurityGroupService securityGroupService = Solon.context().getBean(SecurityGroupService.class);
    private final PortMappingService portMappingService = Solon.context().getBean(PortMappingService.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        Integer domainId = ctx.channel().attr(Constants.SERVER_PORT).get();
        if (null == domainId) {
            // 获取Host请求头
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String httpContent = new String(bytes);
            String domain = HttpUtil.getHostIgnorePort(httpContent);

            log.debug("HttpProxy host: {}", domain);
            if (StringUtils.isBlank(domain)) {
                ctx.channel().close();
                return;
            }
            // 未配置域名解析，不再解析
            if (!ProxyUtil.domainMapingMap.containsKey(domain)) {
                ctx.channel().close();
                return;
            }
            DomainMapping dm = ProxyUtil.domainMapingMap.get(domain);

            // 判断IP是否在该端口绑定的安全组允许的规则内
            String ip = IpUtil.getRealRemoteIp(httpContent);
            if (ip == null) {
                ip = IpUtil.getRemoteIp(ctx);
            }
            if (null==dm.getId() || !securityGroupService.judgeAllow(ip, portMappingService.getSecurityGroupIdByMappingPort(dm.getId()))) {
                // 不在安全组规则放行范围内
                ctx.channel().close();
                return;
            }
            ctx.channel().attr(Constants.REAL_REMOTE_IP).set(ip);
            ctx.channel().attr(Constants.SERVER_PORT).set(dm.getId());
        }

        // 继续传播
        buf.resetReaderIndex();
        ctx.fireChannelRead(buf);
    }

}
