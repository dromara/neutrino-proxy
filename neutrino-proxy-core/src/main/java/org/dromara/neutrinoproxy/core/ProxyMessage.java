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

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Accessors(chain = true)
@Data
public class ProxyMessage {

    /**
     * 心跳消息
     */
    public static final byte TYPE_HEARTBEAT = 0x01;

    /**
     * 认证消息，检测clientKey是否正确
     */
    public static final byte TYPE_AUTH = 0x02;

    /**
     * 代理后端服务器建立连接消息
     */
    public static final byte TYPE_CONNECT = 0x03;

    /**
     * 代理后端服务器断开连接消息
     */
    public static final byte TYPE_DISCONNECT = 0x04;

    /**
     * 代理数据传输
     */
    public static final byte TYPE_TRANSFER = 0x05;

    /**
     * 通用异常信息
     */
    public static final byte TYPE_ERROR = 0x06;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 消息流水号
     */
    private long serialNumber;

    /**
     * 消息命令请求信息
     */
    private String info;

    /**
     * 消息传输数据
     */
    private byte[] data;

    @Override
    public String toString() {
        return "ProxyMessage [type=" + type + ", serialNumber=" + serialNumber + ", info=" + info + ", data=" + Arrays.toString(data) + "]";
    }

    public static ProxyMessage create() {
        return new ProxyMessage();
    }

    public static ProxyMessage buildHeartbeatMessage() {
        return create().setType(TYPE_HEARTBEAT);
    }

    public static ProxyMessage buildAuthMessage(String info, String clientId) {
        return create().setType(TYPE_AUTH)
            .setInfo(info + "," + clientId);
    }

    public static ProxyMessage buildAuthResultMessage(Integer code, String msg, String licenseKey) {
        JSONObject data = new JSONObject();
        data.put("code", code);
        data.put("msg", msg);
        data.put("licenseKey", licenseKey);
        return create().setType(TYPE_AUTH)
            .setInfo(data.toJSONString());
    }

    public static ProxyMessage buildConnectMessage(String visitorId) {
        return create().setType(TYPE_CONNECT)
            .setInfo(visitorId);
    }

    public static ProxyMessage buildDisconnectMessage(String info) {
        return create().setType(TYPE_DISCONNECT)
            .setInfo(info);
    }

    public static ProxyMessage buildTransferMessage(String visitorId, byte[] data) {
        return create().setType(TYPE_TRANSFER)
            .setInfo(visitorId)
            .setData(data);
    }

    public static ProxyMessage buildErrMessage(ExceptionEnum exceptionEnum, String info) {
        JSONObject data = new JSONObject();
        data.put("code", exceptionEnum.getCode());
        data.put("msg", exceptionEnum.getMsg());
        data.put("info", info);
        return create().setType(TYPE_ERROR)
            .setInfo(data.toJSONString());
    }

    public static ProxyMessage buildErrMessage(ExceptionEnum exceptionEnum) {
       return buildErrMessage(exceptionEnum, null);
    }
}
