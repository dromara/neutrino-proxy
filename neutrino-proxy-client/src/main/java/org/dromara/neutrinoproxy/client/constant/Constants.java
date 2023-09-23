package org.dromara.neutrinoproxy.client.constant;

import io.netty.util.AttributeKey;
import org.dromara.neutrinoproxy.client.util.UdpChannelBindInfo;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
public interface Constants {
    AttributeKey<UdpChannelBindInfo> UDP_CHANNEL_BIND_KEY = AttributeKey.newInstance("udpChannelBindKey");

}
