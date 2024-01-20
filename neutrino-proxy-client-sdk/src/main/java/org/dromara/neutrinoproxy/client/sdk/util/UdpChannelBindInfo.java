package org.dromara.neutrinoproxy.client.sdk.util;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
@Accessors(chain = true)
@Data
public class UdpChannelBindInfo {
    private Channel tunnelChannel;
    private LockChannel lockChannel;
    private String visitorId;
    private String visitorIp;
    private int visitorPort;
    private int serverPort;
    private String targetIp;
    private int targetPort;
}
