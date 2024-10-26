package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Getter
@AllArgsConstructor
public enum ClientConnectTypeEnum {
    CONNECT(1, "连接"),
    DISCONNECT(2, "断开连接");

    private Integer type;
    private String desc;
}
