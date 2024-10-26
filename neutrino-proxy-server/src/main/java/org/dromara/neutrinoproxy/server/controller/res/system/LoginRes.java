package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录响应参数
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Accessors(chain = true)
@Data
public class LoginRes {
	/**
	 * token
	 */
	private String token;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 用户名
	 */
	private String userName;
}
