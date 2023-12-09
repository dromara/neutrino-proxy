package org.dromara.neutrinoproxy.core.util;

import cn.hutool.core.net.Ipv4Util;
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

    /**
     * 工作原理为从http协议的header中找公网地址，此主要用于处理nginx转发时塞进去的真实IP的header，找不到返回null
     * @param httpContent http协议文档内容
     * @return 返回找到的第一个公网地址
     */
    public static String getRealRemoteIp(String httpContent) {
        String headerContent = httpContent.split("\r\n\r\n")[0];
        String[] lines = headerContent.split("\r\n");
        String firstLine = lines[0];
        if (!(firstLine.endsWith("HTTP/1.1") || firstLine.endsWith("HTTP/1.0"))) {
            return null;
        }
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            // 匹配有ipv4地址格式的header
            if (!line.matches(".*(\\d+\\.){3}\\d+")) {
                continue;
            }
            // 截取IP地址
            String ip = line.substring(line.charAt(':'));
            if (!Ipv4Util.isInnerIP(ip)) {
                return ip;
            }
        }
        return null;
    }



}
