package org.dromara.neutrinoproxy.server.base.rest;

import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 服务异常
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Getter
public class ServiceException extends RuntimeException {
	/**
	 * 错误码
	 */
	private int code;
	/**
	 * 异常消息
	 */
	private String msg;

	public ServiceException(int code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

	public static ServiceException create(ExceptionConstant constant) {
		return new ServiceException(constant.getCode(), constant.getMsg());
	}

	public static ServiceException create(ExceptionConstant constant, Object... params) {
		return new ServiceException(constant.getCode(), format(constant.getMsg(), params));
	}

	/**
	 * 字符串格式化
	 * @param template
	 * @param params
	 * @return
	 */
	public static String format(String template, Object[] params) {
		if (StringUtils.isEmpty(template) || null == params || params.length == 0) {
			return template;
		}
		String result = template;
		for (Object param : params) {
			int index = result.indexOf("{}");
			if (index == -1) {
				return result;
			}
			result = result.substring(0, index) + param + result.substring(index + 2);
		}
		return result;
	}
}
