package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

@Data
public class SecurityGroupUpdateReq {

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
