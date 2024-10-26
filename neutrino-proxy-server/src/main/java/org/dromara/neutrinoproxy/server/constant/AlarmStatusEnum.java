package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启用状态枚举
 * @author: zCans
 * @date: 2022/9/25
 */
@Getter
@AllArgsConstructor
public enum AlarmStatusEnum {
	NOT(0, "-"),
	WAIT(1, "待发送"),
	SUCCESS(2, "发送成功"),
	ERROR(3, "发送失败");

	private Integer status;
	private String desc;
}
