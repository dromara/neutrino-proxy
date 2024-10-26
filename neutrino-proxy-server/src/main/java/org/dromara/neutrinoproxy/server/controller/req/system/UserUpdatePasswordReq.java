package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/28
 */
@Data
public class UserUpdatePasswordReq {
	private Integer id;
	private String oldLoginPassword;
	private String loginPassword;
}
