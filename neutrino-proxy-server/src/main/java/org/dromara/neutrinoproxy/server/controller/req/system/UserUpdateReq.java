package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/28
 */
@Data
public class UserUpdateReq {
	private Integer id;
	private String name;
	private String loginName;
}
