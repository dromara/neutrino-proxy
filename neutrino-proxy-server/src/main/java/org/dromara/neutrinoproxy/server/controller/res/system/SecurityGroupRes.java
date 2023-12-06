package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SecurityGroupRes {

    private Integer id;

    /**
     * 组名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private String enable;

    /**
     * 默认放行类型
     * {@link SecurityRulePassTypeEnum}
     */
    private String defaultPassType;

    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;



}
