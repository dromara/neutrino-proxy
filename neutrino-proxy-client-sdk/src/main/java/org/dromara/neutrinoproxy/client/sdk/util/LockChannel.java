package org.dromara.neutrinoproxy.client.sdk.util;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2023/9/21
 */
@Accessors(chain = true)
@Data
public class LockChannel {
    // 端口号
    private int port;
    // 通道
    private Channel channel;
    // 期望的响应次数
    private int proxyResponses;
    // 超时时间（毫秒）
    private long proxyTimeoutMs;
    // 被获取的时间
    private Date takeTime;
    // 已经响应的次数
    private int responseCount;
}
