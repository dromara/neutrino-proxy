package org.dromara.neutrinoproxy.client.util;

import io.netty.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.client.config.ProxyConfig;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
@Slf4j
public class UdpServerUtil {
    private static final Boolean isSupportUdp = Boolean.FALSE;
    private static int udpServerPortMin = 0;
    private static int udpServerPortMax = 0;
    private static int nextUdpServerPort = 0;
    private static Bootstrap udpServerBootstrap;
    private static final String defaultUdpServerKey = "default";

    /**
     * 初始化UDP缓存
     * 1、初始化一个基础UDP服务，用于不需要响应的UDP转发
     * 2、维护一个UDP服务池，用于需要响应的UDP转发
     * @param proxyConfig
     */
    public static void initCache(ProxyConfig proxyConfig, Bootstrap udpServerBootstrap) {
        if (null == proxyConfig.getClient().getUdp() || StringUtils.isEmpty(proxyConfig.getClient().getUdp().getPuppetPortRange())) {
            return;
        }
        ProxyConfig.Udp udpConfig = proxyConfig.getClient().getUdp();
        if (StringUtils.isEmpty(udpConfig.getPuppetPortRange())) {
            return;
        }
        String[] tmp = udpConfig.getPuppetPortRange().split("-");
        if (null == tmp || tmp.length != 2) {
            log.error("client udp config error!");
            return;
        }
        try {
            udpServerPortMin = Integer.parseInt(tmp[0]);
            udpServerPortMax = Integer.parseInt(tmp[1]);
            if (udpServerPortMax <= udpServerPortMin) {
                // 至少得给2个udp端口，一个用于基础无响应UDP转发，一个用于有响应UDP转发
                throw new RuntimeException("client udp config error!");
            }
            nextUdpServerPort = udpServerPortMin;
            UdpServerUtil.udpServerBootstrap = udpServerBootstrap;
            log.info("udp proxy server port: {} ~ {}", udpServerPortMin, udpServerPortMax);
        } catch (Exception e) {
            log.error("client udp config error!", e);
            return;
        }
    }

    public static Boolean hasNextUdpServerPort() {
        return nextUdpServerPort <= udpServerPortMax;
    }

    public static synchronized int nextUdpServerPort() {
        return nextUdpServerPort++;
    }
}
