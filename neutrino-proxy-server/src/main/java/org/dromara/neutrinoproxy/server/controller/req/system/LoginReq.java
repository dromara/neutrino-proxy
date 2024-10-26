package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 登录请求参数
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Data
public class LoginReq {
	/**
	 * 登录名
	 */
	private String loginName;
	/**
	 * 登录密码
	 */
	private String loginPassword;
}
