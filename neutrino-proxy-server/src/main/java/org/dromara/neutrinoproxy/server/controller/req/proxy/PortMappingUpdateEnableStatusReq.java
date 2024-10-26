package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

/**
 * 端口映射更新启用状态请求
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Data
public class PortMappingUpdateEnableStatusReq {

	/**
	 * id
	 */
	private Integer id;
	/**
	 * 启用状态
	 */
	private Integer enable;

}
