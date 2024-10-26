package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 启用状态枚举
 * @author: aoshiguchen
 * @date: 2022/8/5
 */
@Getter
@AllArgsConstructor
public enum EnableStatusEnum {
	ENABLE(1, "启用"),
	DISABLE(2, "禁用");
	private static Map<Integer, EnableStatusEnum> CACHE = Stream.of(EnableStatusEnum.values()).collect(Collectors.toMap(EnableStatusEnum::getStatus, Function.identity()));

	private Integer status;
	private String desc;
	public static EnableStatusEnum of(Integer status) {
		return CACHE.get(status);
	}
}
