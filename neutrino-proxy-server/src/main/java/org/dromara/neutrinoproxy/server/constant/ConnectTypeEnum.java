package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 连接类型枚举
 * @author: aoshiguchen
 * @date: 2022/8/31
 */
@Getter
@AllArgsConstructor
public enum ConnectTypeEnum {
	CONNECT(1, "连接"),
	DISCONNECT(2, "端开连接");
	private Integer type;
	private String desc;
}
