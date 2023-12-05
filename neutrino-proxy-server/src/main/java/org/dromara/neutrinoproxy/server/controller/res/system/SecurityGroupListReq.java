package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;

@Data
public class SecurityGroupListReq {

    private Integer id;

    /**
     * 组名
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
