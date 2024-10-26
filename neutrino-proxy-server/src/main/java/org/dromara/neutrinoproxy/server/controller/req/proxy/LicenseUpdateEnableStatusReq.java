package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;

/**
 * 更新启用状态请求
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Data
public class LicenseUpdateEnableStatusReq {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 启用状态
	 * {@link EnableStatusEnum}
	 */
	private Integer enable;
}
