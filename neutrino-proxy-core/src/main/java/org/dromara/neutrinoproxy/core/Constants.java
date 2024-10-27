package org.dromara.neutrinoproxy.core;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface Constants {

    AttributeKey<Channel> NEXT_CHANNEL = AttributeKey.newInstance("nxt_channel");

    AttributeKey<String> VISITOR_ID = AttributeKey.newInstance("visitor_id");

    AttributeKey<Integer> LICENSE_ID = AttributeKey.newInstance("license_id");

    AttributeKey<String> TARGET_IP = AttributeKey.newInstance("targetIp");
    AttributeKey<Integer> TARGET_PORT = AttributeKey.newInstance("targetPort");
    AttributeKey<Integer> PROXY_RESPONSES = AttributeKey.newInstance("proxyResponses");
    AttributeKey<Long> PROXY_TIMEOUT_MS = AttributeKey.newInstance("proxyTimeoutMs");

    // 临时解决 udp channel被close的问题
    AttributeKey<Boolean> IS_UDP_KEY =  AttributeKey.newInstance("isUdp");
    AttributeKey<InetSocketAddress> SENDER = AttributeKey.newInstance("sender");

    AttributeKey<Integer> SERVER_PORT = AttributeKey.newInstance("serverPort");
    AttributeKey<String> REAL_REMOTE_IP = AttributeKey.newInstance("realRemoteIp");


    AttributeKey<Boolean> FLOW_LIMITER_FLAG = AttributeKey.newInstance("flowLimiterFlag");


    int HEADER_SIZE = 4;
    int TYPE_SIZE = 1;
    int SERIAL_NUMBER_SIZE = 8;
    int INFO_LENGTH_SIZE = 4;

    interface ProxyDataTypeName {
        String HEARTBEAT = "HEARTBEAT";
        String AUTH = "AUTH";
        String CONNECT = "CONNECT";
        String DISCONNECT = "DISCONNECT";
        String TRANSFER = "TRANSFER";
        String UDP_CONNECT = "UDP_CONNECT";
        String UDP_DISCONNECT = "UDP_DISCONNECT";
        String UDP_TRANSFER = "UDP_TRANSFER";
        String ERROR = "ERROR";
        String PORT_MAPPING_SYNC = "PORT_MAPPING_SYNC";
    }
}
