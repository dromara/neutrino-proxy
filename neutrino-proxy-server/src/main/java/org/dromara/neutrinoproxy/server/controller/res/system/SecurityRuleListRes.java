package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;

@Data
@ToString
@Accessors(chain = true)
public class SecurityRuleListRes {

    private Integer id;

    /**
     * 所属安全组
     */
    private Integer groupId;

    /**
     * 规则名
     */
    private String name;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 规则,ipv6只支持单个ip判断
     * 单个ip：192.168.1.1,0:0:0:0:0:0:10.0.0.1
     * 范围类型：192.168.1.0-192.168.1.255
     * 掩码类型：192.168.1.0/24
     * 泛型：0.0.0.0/ALL
     * 每个类型中间以英文逗号分隔
     */
    private String rule;

    /**
     * 放行类型，reject 或 allow
     * {@link SecurityRulePassTypeEnum}
     */
    private SecurityRulePassTypeEnum passType;

    /**
     * 优先级，数字越小，优先级越高
     */
    private Integer priority;

}
