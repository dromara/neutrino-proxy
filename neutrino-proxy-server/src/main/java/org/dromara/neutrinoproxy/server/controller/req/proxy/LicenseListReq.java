package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Data
public class LicenseListReq {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 是否在线
     */
    private Integer isOnline;
    /**
     * 启动状态 1启用 2禁用
     */
    private Integer enable;
}
