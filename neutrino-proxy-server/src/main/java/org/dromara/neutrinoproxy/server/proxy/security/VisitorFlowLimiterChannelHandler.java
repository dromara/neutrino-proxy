package org.dromara.neutrinoproxy.server.proxy.security;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.server.service.LicenseService;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.dromara.neutrinoproxy.server.service.bo.FlowLimitBO;
import org.noear.solon.Solon;

/**
 * 访问者流量限制器
 * @author: aoshiguchen
 * @date: 2023/12/15
 */
@Slf4j
public class VisitorFlowLimiterChannelHandler extends ChannelInboundHandlerAdapter {

    private final LicenseService licenseService = Solon.context().getBean(LicenseService.class);
    private final PortMappingService portMappingService = Solon.context().getBean(PortMappingService.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Boolean flowLimiterFlag = ctx.channel().attr(Constants.FLOW_LIMITER_FLAG).get();

        if (null == flowLimiterFlag || !flowLimiterFlag) {
            Integer serverPort = ctx.channel().attr(Constants.SERVER_PORT).get();
            Long upLimitRate = null;
            Long downLimitRate = null;
            // 先获取端口映射上的限速设置
            FlowLimitBO flowLimitBO = portMappingService.getFlowLimitByServerPort(serverPort);
            if (null != flowLimitBO) {
                upLimitRate = flowLimitBO.getUpLimitRate();
                downLimitRate = flowLimitBO.getDownLimitRate();
            }
            if (null != upLimitRate || null != downLimitRate) {
                // 如果不全为空，则需要做限速
                ctx.pipeline().addAfter("security", "trafficShaping", new ChannelTrafficShapingHandler(downLimitRate == null ? 0 : downLimitRate, upLimitRate == null ? 0 : upLimitRate, 100, 600000));
            }

            // 每个连接第一次处理之后。无论是否限速，该连接后续都不在处理，避免频繁执行影响性能
            ctx.channel().attr(Constants.FLOW_LIMITER_FLAG).set(Boolean.TRUE);
        }

        // 继续传播
        ctx.fireChannelRead(msg);
    }

}
