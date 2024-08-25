package org.dromara.neutrinoproxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.neutrinoproxy.server.dal.entity.DomainNameDO;
import org.dromara.neutrinoproxy.server.service.bo.FullDomainNameBO;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Mapper
public interface DomainMapper extends BaseMapper<DomainNameDO> {
    default DomainNameDO checkRepeat(String domain, Set<Integer> excludeIds) {
        return this.selectOne(new LambdaQueryWrapper<DomainNameDO>()
            .eq(DomainNameDO::getDomain, domain)
            .notIn(excludeIds != null, DomainNameDO::getId, excludeIds)
            .last("limit 1")
        );
    }

    default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
        this.update(Wrappers.<DomainNameDO>lambdaUpdate()
            .eq(DomainNameDO::getId, id)
            .set(DomainNameDO::getEnable, enable)
            .set(DomainNameDO::getUpdateTime, updateTime)
        );
    }


    List<FullDomainNameBO> selectFullDomainNameListByPortMappingIds(@Param("ids") Set<Integer> ids);

    List<FullDomainNameBO> selectFullDomainNameListByDomainNameIds(@Param("ids") Set<Integer> ids);

    List<FullDomainNameBO> selectFullDomainNameList();
}
