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
        // 查询与输入域名相关的域名列表
        List<DomainNameDO> domainList = this.selectList(new LambdaQueryWrapper<DomainNameDO>()
            .notIn(excludeIds != null, DomainNameDO::getId, excludeIds)
        );

        // 遍历数据库中的域名，检查是否存在重复（包括子域名）
        for (DomainNameDO domainNameDO : domainList) {
            String existingDomain = domainNameDO.getDomain();
            // 完全相等的情况
            if (existingDomain.equals(domain)) {
                return domainNameDO;
            }
//            // 是子域名关系
//            if (isSubdomainOrEqual(existingDomain, domain)) {
//                return domainNameDO;
//            }
        }

        // 如果没有找到重复的，返回 null
        return null;
    }

    // 辅助方法：判断 domain1 是否是 domain2 的子域名，或者两者相等
    private boolean isSubdomainOrEqual(String domain1, String domain2) {
        // 判断是否是子域名关系，例如 "proxy.asgc.fun" 是 "asgc.fun" 的子域名
        return domain1.endsWith("." + domain2) || domain2.endsWith("." + domain1);
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
