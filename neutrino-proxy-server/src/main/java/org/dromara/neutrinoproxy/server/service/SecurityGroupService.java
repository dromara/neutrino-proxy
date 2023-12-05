package org.dromara.neutrinoproxy.server.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;
import org.dromara.neutrinoproxy.server.dal.SecurityGroupMapper;
import org.dromara.neutrinoproxy.server.dal.SecurityRuleMapper;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityGroupDO;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityRuleDO;
import org.noear.solon.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecurityGroupService {

    @Db
    private SecurityGroupMapper securityGroupMapper;

    @Db
    private SecurityRuleMapper securityRuleMapper;

    private Map<Integer, SecurityGroupDO> securityGroupMap = new ConcurrentHashMap<>();

    // 允许通过控制的缓存，缓存类型最近最久未使用缓存，容量100，超时时间5分钟
    private Cache<String, Boolean> ipAllowControlCache = CacheUtil.newLRUCache(100, 1000 * 60 * 5);

    public void init() {
        List<SecurityGroupDO> groupDOList = securityGroupMapper.selectList(Wrappers.lambdaQuery(SecurityGroupDO.class));
        groupDOList.forEach(securityGroupDO -> securityGroupMap.put(securityGroupDO.getId(), securityGroupDO));
    }

    public boolean judgeAllow(String ip, Integer groupId) {

        SecurityGroupDO groupDO = securityGroupMap.get(groupId);
        if (groupDO == null || groupDO.getEnable() == EnableStatusEnum.DISABLE) {
            return true;
        }

        String judgeAllowMapKey = ip + groupId;
        if (ipAllowControlCache.containsKey(judgeAllowMapKey)) {
            return ipAllowControlCache.get(judgeAllowMapKey);
        }

        List<SecurityRuleDO>  ruleDOList = securityRuleMapper.selectList(Wrappers.lambdaQuery(SecurityRuleDO.class)
            .eq(SecurityRuleDO::getGroupId, groupId)
            .orderByAsc(SecurityRuleDO::getPriority)
        );
        Boolean allow = null;
        for (SecurityRuleDO ruleDO : ruleDOList) {
            SecurityRulePassTypeEnum passType = ruleDO.allow(ip);
            if (passType == SecurityRulePassTypeEnum.ALLOW) {
                allow = true;
                break;
            }
            if (passType == SecurityRulePassTypeEnum.DENY) {
                allow = false;
                break;
            }
        }

        if (allow == null) {
            allow = true;
        }

        // 当前IP没有匹配到任何一条规则，则放行
        ipAllowControlCache.put(judgeAllowMapKey, allow);

        return allow;
    }
}
