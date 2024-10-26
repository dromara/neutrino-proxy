package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/28
 */
@Data
public class UserUpdateEnableStatusReq {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 启用状态
	 */
	private Integer enable;
}
