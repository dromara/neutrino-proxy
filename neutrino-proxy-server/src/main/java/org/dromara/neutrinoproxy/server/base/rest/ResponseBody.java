package org.dromara.neutrinoproxy.server.base.rest;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 响应体
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Accessors(chain = true)
@Data
public class ResponseBody<T> {
	private Integer code;
	private String msg;
	private T data;
	private String stack;
}
