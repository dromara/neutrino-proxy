package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * @author: wen.y
 * @date: 2023/12/10
 */
@Data
public class SecurityGroupListReq {
    private String name;
    private Integer enable;
}
