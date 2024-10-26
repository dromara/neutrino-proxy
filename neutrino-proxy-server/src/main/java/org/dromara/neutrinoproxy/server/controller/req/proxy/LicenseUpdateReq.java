package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Data
public class LicenseUpdateReq {
	/**
	 * id
	 */
	private Integer id;
	/**
	 * license名称
	 */
	private String name;
    /**
     * 上传限速
     */
    private String upLimitRate;
    /**
     * 下载限速
     */
    private String downLimitRate;
}
