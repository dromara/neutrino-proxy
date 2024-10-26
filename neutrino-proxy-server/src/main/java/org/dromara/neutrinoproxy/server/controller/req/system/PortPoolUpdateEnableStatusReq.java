package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 端口池更新启用状态请求
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Data
public class PortPoolUpdateEnableStatusReq {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 启用状态
	 */
	private Integer enable;
}
