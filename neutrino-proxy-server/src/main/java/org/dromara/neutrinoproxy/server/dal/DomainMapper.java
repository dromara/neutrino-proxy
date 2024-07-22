package org.dromara.neutrinoproxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.neutrinoproxy.server.controller.req.system.PortGroupListReq;
import org.dromara.neutrinoproxy.server.controller.res.system.PortGroupListRes;
import org.dromara.neutrinoproxy.server.dal.entity.DomainNameDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortGroupDO;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Mapper
public interface DomainMapper extends BaseMapper<DomainNameDO> {
    default DomainNameDO checkRepeat(String domain, Set<Integer> excludeIds) {
        return this.selectOne(new LambdaQueryWrapper<DomainNameDO>()
            .eq(DomainNameDO::getDomain, domain)
            .notIn(DomainNameDO::getId, excludeIds)
            .last("limit 1")
        );
    }
}
