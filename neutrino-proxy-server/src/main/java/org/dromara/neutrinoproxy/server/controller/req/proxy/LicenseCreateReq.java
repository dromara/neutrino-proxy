package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

/**
 * license创建请求
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Data
public class LicenseCreateReq {
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 用户ID
	 */
	private Integer userId;
    /**
     * 上传限速
     */
    private String upLimitRate;
    /**
     * 下载限速
     */
    private String downLimitRate;
}
