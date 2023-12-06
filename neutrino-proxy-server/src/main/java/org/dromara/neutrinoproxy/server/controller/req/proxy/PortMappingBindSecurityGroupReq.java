package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

@Data
public class PortMappingBindSecurityGroupReq {

    private Integer id;

    private Integer securityGroupId;

}
