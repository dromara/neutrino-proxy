package org.dromara.neutrinoproxy.core;

import lombok.Data;
import lombok.experimental.Accessors;
import org.noear.snack.ONode;

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
     * UDP代理隧道连接
     */
    public static final byte TYPE_UDP_CONNECT = 0x08;
    /**
     * UDP代理隧道断开连接
     */
    public static final byte TYPE_UDP_DISCONNECT = 0x09;
    /**
     * UDP数据传输
     */
    public static final byte TYPE_UDP_TRANSFER = 0x10;

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
        ONode data = ONode.newObject();
        data.set("code", code);
        data.set("msg", msg);
        data.set("licenseKey", licenseKey);
        return create().setType(TYPE_AUTH)
            .setInfo(data.toJson());
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


    public static ProxyMessage buildUdpConnectMessage(UdpBaseInfo info) {
        return create().setType(TYPE_UDP_CONNECT)
                .setInfo(info.toJsonString());
    }

    public static ProxyMessage buildUdpDisconnectMessage() {
        return create().setType(TYPE_UDP_DISCONNECT);
    }

    public static ProxyMessage buildUdpTransferMessage(UdpBaseInfo info) {
        return create().setType(TYPE_UDP_TRANSFER)
                .setInfo(info.toJsonString());
    }

    public static ProxyMessage buildErrMessage(ExceptionEnum exceptionEnum, String info) {
        ONode data = ONode.newObject();
        data.set("code", exceptionEnum.getCode());
        data.set("msg", exceptionEnum.getMsg());
        data.set("info", info);
        return create().setType(TYPE_ERROR)
            .setInfo(data.toJson());
    }

    public static ProxyMessage buildErrMessage(ExceptionEnum exceptionEnum) {
       return buildErrMessage(exceptionEnum, null);
    }

    @Accessors(chain = true)
    @Data
    public static class UdpBaseInfo {
        private String visitorId;
        private String visitorIp;
        private int visitorPort;
        private int serverPort;
        private String targetIp;
        private int targetPort;
        /**
         * 期待的响应数
         */
        private int proxyResponses;
        /**
         * 超时时间(<=0时，相当于不需要响应)
         */
        private long proxyTimeoutMs;
        public String toJsonString() {
            return ONode.serialize(this);
        }
    }
}
