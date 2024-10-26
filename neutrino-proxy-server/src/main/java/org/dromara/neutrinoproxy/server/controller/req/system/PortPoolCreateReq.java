package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 端口池创建请求
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Data
public class PortPoolCreateReq {
	private String port;

	private Integer groupId;
}
