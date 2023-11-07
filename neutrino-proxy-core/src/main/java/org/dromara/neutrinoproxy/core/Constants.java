/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    AttributeKey<String> SECURE_PRIVATE_KEY = AttributeKey.newInstance("secure_private_key");

    AttributeKey<byte[]> SECURE_KEY = AttributeKey.newInstance("secure_key");

    AttributeKey<Boolean> IS_SECURITY = AttributeKey.newInstance("is_security");

    AttributeKey<Integer> LICENSE_ID = AttributeKey.newInstance("license_id");

    AttributeKey<String> TARGET_IP = AttributeKey.newInstance("targetIp");
    AttributeKey<Integer> TARGET_PORT = AttributeKey.newInstance("targetPort");
    AttributeKey<Integer> PROXY_RESPONSES = AttributeKey.newInstance("proxyResponses");
    AttributeKey<Long> PROXY_TIMEOUT_MS = AttributeKey.newInstance("proxyTimeoutMs");

    // 临时解决 udp channel被close的问题
    AttributeKey<Boolean> IS_UDP_KEY =  AttributeKey.newInstance("isUdp");
    AttributeKey<InetSocketAddress> SENDER = AttributeKey.newInstance("sender");


    int HEADER_SIZE = 4;
    int TYPE_SIZE = 1;
    int SERIAL_NUMBER_SIZE = 8;
    int INFO_LENGTH_SIZE = 4;

    interface ProxyDataTypeName {
        String HEARTBEAT = "HEARTBEAT";
        String SECURE_KEY = "SECURE_KEY";
        String IS_SECURITY = "IS_SECURITY";
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
