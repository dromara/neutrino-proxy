package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * @author: aoshiguchen
 * @date: 2023/12/10
 */
@Data
public class SecurityRuleListReq {
    private String groupId;
    private String name;
    private String description;
    private Integer passType;
    private Integer enable;
}
