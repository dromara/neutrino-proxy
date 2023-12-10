package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;

/**
 * @author: wen.y
 * @date: 2023/12/10
 */
@Data
public class SecurityGroupListReq {
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 通过类型
     */
    private SecurityRulePassTypeEnum defaultPassType;
    private Integer enable;
}
