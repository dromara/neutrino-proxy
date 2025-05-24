package org.dromara.neutrinoproxy.server.proxy.security;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.i18nformatter.qual.I18nFormat;
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

    // 拼接收到的 ByteBuf 内容
    private ByteBuf cumulationBuf = Unpooled.buffer();
//    private boolean initialized = false;

    public HttpVisitorSecurityChannelHandler(Boolean isHttps) {
        this.isHttps = isHttps;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (initialized) {
//            // 已经初始化，直接透传
//            ctx.fireChannelRead(msg);
//            return;
//        }

        // 累加数据包
        ByteBuf buf = (ByteBuf) msg;
        cumulationBuf.writeBytes(buf);

        String dataStr = cumulationBuf.toString(CharsetUtil.UTF_8);
        int headerEndIndex = dataStr.indexOf("\r\n\r\n");
        if (-1 == headerEndIndex) {
            // 请求头还没读完，继续等
            return;
        }

        // 获取Host请求头
        String headerPart = dataStr.substring(0, headerEndIndex + 4);
        String host = HttpUtil.getHostIgnorePort(headerPart); //test1.asgc.fun

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
            String ip = IpUtil.getRealRemoteIp(headerPart);
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

        // 从此之后所有的 in 都是“直接透传”。
        // 否则，若请求体过长，数据包被拆分为多个，后续的数据包都无法解析出host，导致转发数据不完整。
        ctx.pipeline().remove(this);

        // 继续传播
        ctx.fireChannelRead(cumulationBuf);
    }
}
