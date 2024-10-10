package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

/**
 * @author Mirac
 * @date 23/7/2024
 */
@Data
public class DomainUpdateEnableStatusReq {

    /**
     * id
     */
    private Integer id;

    /**
     * 启用状态
     */
    private Integer enable;

    /**
     * 域名
     */
    private String domain;
}
