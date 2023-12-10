package org.dromara.neutrinoproxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingListReq;
import org.dromara.neutrinoproxy.server.controller.req.system.SecurityGroupListReq;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityGroupDO;

import java.util.Date;
import java.util.List;

public interface SecurityGroupMapper extends BaseMapper<SecurityGroupDO> {
    List<SecurityGroupDO> selectByCondition(IPage<SecurityGroupDO> page, @Param("req") SecurityGroupListReq req);

    default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
        this.update(null, new LambdaUpdateWrapper<SecurityGroupDO>()
            .eq(SecurityGroupDO::getId, id)
            .set(SecurityGroupDO::getEnable, enable)
            .set(SecurityGroupDO::getUpdateTime, updateTime)
        );
    }
}
