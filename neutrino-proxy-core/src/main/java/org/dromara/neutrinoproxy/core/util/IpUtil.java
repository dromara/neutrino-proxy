package org.dromara.neutrinoproxy.core.util;

import io.netty.channel.ChannelHandlerContext;

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

}
