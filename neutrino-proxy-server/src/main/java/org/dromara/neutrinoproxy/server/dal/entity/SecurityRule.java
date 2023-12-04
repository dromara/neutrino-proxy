package org.dromara.neutrinoproxy.server.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
@TableName("security_rule")
public class SecurityRule {

    @TableId(type = IdType.AUTO)
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
     * 规则
     * 范围类型：192.168.1.0-192.168.1.255
     * 掩码类型：192.168.1.0/24
     * 泛型：0.0.0.0
     * 单个ip：192.168.1.1
     * 每个类型中间以英文逗号分隔
     */
    private String rule;

    /**
     * 放行类型，reject 或 allow
     * {@link SecurityRulePassTypeEnum}
     */
    private Integer passType;

    /**
     * 优先级，数字越小，优先级越高
     */
    private Integer priority;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private Integer enable;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

}
