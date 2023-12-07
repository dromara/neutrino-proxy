package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;

@Data
public class SecurityGroupCreateReq {
    /**
     * 组名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 通过类型
     */
    private SecurityRulePassTypeEnum defaultPassType;

}
