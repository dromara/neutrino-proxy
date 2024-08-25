package org.dromara.neutrinoproxy.server.dal;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.neutrinoproxy.server.dal.entity.DomainNameDO;
import org.dromara.neutrinoproxy.server.dal.entity.DomainPortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.service.bo.FullDomainNameBO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author: Mirac
 * @date: 2024/8/24
 */
@Mapper
public interface DomainPortMappingMapper extends BaseMapper<DomainPortMappingDO> {
    default Boolean checkRepeatBySubdomain(String subdomain, Integer domainNameId, Integer portMappingId) {
        return StringUtils.isEmpty(subdomain)? Boolean.FALSE : this.selectCount(new LambdaQueryWrapper<DomainPortMappingDO>()
            .eq(DomainPortMappingDO::getSubdomain, subdomain)
            .eq(DomainPortMappingDO::getDomainNameId, domainNameId)
            .ne(portMappingId != null, DomainPortMappingDO::getPortMappingId, portMappingId)
            .last("limit 1")
        ).intValue() > 0;
    }

    default List<DomainPortMappingDO> findByPortMappingId(Integer portMappingId) {
        return this.selectList(Wrappers.<DomainPortMappingDO>lambdaQuery()
            .eq(DomainPortMappingDO::getPortMappingId, portMappingId));
    }

    default void deleteByDomainNameId(Integer doomainNameId) {
        this.delete(Wrappers.<DomainPortMappingDO>lambdaQuery()
            .eq(DomainPortMappingDO::getDomainNameId, doomainNameId));
    }

    default boolean checkUsed(Integer domainNameId) {
        return this.exists(Wrappers.<DomainPortMappingDO>lambdaQuery()
            .eq(DomainPortMappingDO::getDomainNameId, domainNameId));
    }
}
