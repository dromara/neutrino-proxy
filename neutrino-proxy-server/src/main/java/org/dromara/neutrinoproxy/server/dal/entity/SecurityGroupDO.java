package org.dromara.neutrinoproxy.server.dal.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;
import org.dromara.neutrinoproxy.server.controller.res.system.SecurityGroupDetailRes;
import org.dromara.neutrinoproxy.server.controller.res.system.SecurityGroupListRes;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
@TableName("security_group")
public class SecurityGroupDO {

    @TableId(type = IdType.AUTO)
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
     * 用户id
     */
    private Integer userId;
    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private Integer enable;

    /**
     * 默认放行类型
     * {@link SecurityRulePassTypeEnum}
     */
    private SecurityRulePassTypeEnum defaultPassType;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    public SecurityGroupListRes toListRes() {
        SecurityGroupListRes res = new SecurityGroupListRes();
        BeanUtil.copyProperties(this, res);
        res.setDefaultPassType(defaultPassType.getDesc())
            .setCreateTime(DateUtil.format(this.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT))
            .setUpdateTime(DateUtil.format(this.getUpdateTime(), DatePattern.NORM_DATETIME_FORMAT));
        return res;
    }

    public SecurityGroupDetailRes toDetailRes() {
        SecurityGroupDetailRes res = new SecurityGroupDetailRes();
        BeanUtil.copyProperties(this, res);
        res.setDefaultPassType(defaultPassType.getDesc())
            .setCreateTime(DateUtil.format(this.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT))
            .setUpdateTime(DateUtil.format(this.getUpdateTime(), DatePattern.NORM_DATETIME_FORMAT));
        return res;
    }
}
