package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;

import java.util.Date;

/**
 * 用户列表响应
 * @author: aoshiguchen
 * @date: 2022/8/14
 */
@Data
public class UserListRes {
	private Integer id;
	/**
	 * 用户名
	 */
	private String name;
	/**
	 * 登录名
	 */
	private String loginName;
	/**
	 * 登录密码
	 */
	private String loginPassword;
	/**
	 * 是否禁用
	 */
	private Integer enable;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
}
