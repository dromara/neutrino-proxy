package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 在线状态枚举
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Getter
@AllArgsConstructor
public enum OnlineStatusEnum {
	ONLINE(1, "在线"),
	OFFLINE(2, "离线");

	private Integer status;
	private String desc;
}
