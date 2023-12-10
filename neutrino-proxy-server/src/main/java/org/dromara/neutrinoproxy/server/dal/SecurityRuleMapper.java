package org.dromara.neutrinoproxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.dromara.neutrinoproxy.server.controller.req.system.SecurityGroupListReq;
import org.dromara.neutrinoproxy.server.controller.req.system.SecurityRuleListReq;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityGroupDO;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityRuleDO;

import java.util.Date;
import java.util.List;

public interface SecurityRuleMapper extends BaseMapper<SecurityRuleDO> {
    List<SecurityRuleDO> selectByCondition(IPage<SecurityRuleDO> page, @Param("req") SecurityRuleListReq req);

    default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<SecurityRuleDO>()
                .eq(SecurityRuleDO::getId, id)
                .set(SecurityRuleDO::getEnable, enable)
                .set(SecurityRuleDO::getUpdateTime, updateTime)
        );
    }
}
