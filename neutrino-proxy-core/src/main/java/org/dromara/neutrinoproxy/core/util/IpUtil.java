package org.dromara.neutrinoproxy.core.util;

import cn.hutool.core.net.Ipv4Util;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

public class IpUtil extends org.noear.solon.core.util.IpUtil {

    public static String getRemoteIp(ChannelHandlerContext ctx) {
        String remoteAddress = "";
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if (socketAddress != null) {
            remoteAddress = socketAddress.getAddress().getHostAddress();
        }
        return remoteAddress;
    }

    /**
     * 工作原理为从http协议的header中找公网地址，此主要用于处理nginx转发时塞进去的真实IP的header，找不到返回null
     * @param httpContent http协议文档内容
     * @return 返回找到的第一个公网地址
     */
    public static String getRealRemoteIp(String httpContent) {
        String ip = HttpUtil.getHeaderValue(httpContent, "X-Forwarded-For");
        if (StringUtils.isEmpty(ip)) {
            ip = HttpUtil.getHeaderValue(httpContent, "X-Real-IP");
        }
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }
        return null;
    }

}
