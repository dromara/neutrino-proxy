package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 调度管理更新启用状态请求
 * @author: zCans
 * @date: 2022/9/12
 */
@Data
public class JobInfoUpdateEnableStatusReq {

	/**
	 * id
	 */
	private Integer id;
	/**
	 * 启用状态
	 */
	private Integer enable;

}
