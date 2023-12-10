package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * @author: aoshiguchen
 * @date: 2023/12/10
 */
@Data
public class SecurityGroupUpdateEnableStatueReq {
    /**
     * id
     */
    private Integer id;
    /**
     * 启用状态
     */
    private Integer enable;
}
