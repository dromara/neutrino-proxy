package org.dromara.neutrinoproxy.server.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.constant.SecurityRulePassTypeEnum;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.dromara.neutrinoproxy.server.dal.SecurityGroupMapper;
import org.dromara.neutrinoproxy.server.dal.SecurityRuleMapper;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityGroupDO;
import org.dromara.neutrinoproxy.server.dal.entity.SecurityRuleDO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.runtime.NativeDetector;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SecurityGroupService {

    @Db
    private SecurityGroupMapper securityGroupMapper;

    @Db
    private SecurityRuleMapper securityRuleMapper;

    private final Map<Integer, SecurityGroupDO> securityGroupMap = new ConcurrentHashMap<>();

    // 允许通过控制的缓存，缓存类型最近最久未使用缓存，容量100，超时时间5分钟
    private final Cache<String, Boolean> ipAllowControlCache = CacheUtil.newLRUCache(100, 1000 * 60 * 5);

    @Init(index = 100)
    public synchronized void init() {
        // aot 阶段，不初始化
        if (NativeDetector.isAotRuntime()) {
            return;
        }
        securityGroupMap.clear();
        List<SecurityGroupDO> groupDOList = securityGroupMapper.selectList(Wrappers.lambdaQuery(SecurityGroupDO.class)
            .eq(SecurityGroupDO::getEnable, EnableStatusEnum.ENABLE.getStatus()));
        groupDOList.forEach(securityGroupDO -> securityGroupMap.put(securityGroupDO.getId(), securityGroupDO));
        ipAllowControlCache.clear();
    }

    public void clearCache() {
        ipAllowControlCache.clear();
    }

    public PageInfo<SecurityGroupListRes> groupPage(PageQuery pageQuery, SecurityGroupListReq req) {
        if (StringUtils.isNotEmpty(req.getName())) {
            //在应用层处理，否则sqlite不支持
            req.setName("%" + req.getName() + "%");
        }
        if (StringUtils.isNotEmpty(req.getDescription())) {
            //在应用层处理，否则sqlite不支持
            req.setDescription("%" + req.getDescription() + "%");
        }
        Page<SecurityGroupDO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        List<SecurityGroupDO> list = securityGroupMapper.selectByCondition(page, req);
        if (CollectionUtils.isEmpty(list)) {
            return PageInfo.of(null, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        List<SecurityGroupListRes> respList = list.stream().map(SecurityGroupDO::toListRes).collect(Collectors.toList());
        return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public List<SecurityGroupListRes> groupList() {
        List<SecurityGroupDO> list = securityGroupMapper.selectList(Wrappers.lambdaQuery(SecurityGroupDO.class)
            .eq(SecurityGroupDO::getUserId, SystemContextHolder.getUserId()));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(SecurityGroupDO::toListRes).collect(Collectors.toList());
    }

    public SecurityGroupDetailRes groupDetail(SecurityGroupDetailReq req) {
        SecurityGroupDO securityGroupDO = securityGroupMapper.selectById(req.getId());
        if (null != securityGroupDO) {
            return securityGroupDO.toDetailRes();
        }
        return null;
    }

    public void createGroup(SecurityGroupCreateReq req) {
        SecurityGroupDO groupDO = new SecurityGroupDO();
        BeanUtil.copyProperties(req, groupDO);
        groupDO.setEnable(EnableStatusEnum.ENABLE.getStatus())
            .setUserId(SystemContextHolder.getUserId())
            .setCreateTime(new Date())
            .setUpdateTime(new Date());
        securityGroupMapper.insert(groupDO);
        init();
    }

    /**
     * 更新时不允许更新默认放行类型
     * @param req 安全组更新参数
     */
    public void updateGroup(SecurityGroupUpdateReq req) {
        SecurityGroupDO groupDO = securityGroupMapper.selectById(req.getId());
        BeanUtil.copyProperties(req, groupDO, "defaultPassType");
        securityGroupMapper.updateById(groupDO);
        init();
    }

    /**
     * 删除安全组，并级联删除安全组下的规则，删除后，需缓存
     * @param groupId 安全组Id
     */
    public void deleteGroup(Integer groupId) {
        securityGroupMapper.deleteById(groupId);
        securityRuleMapper.delete(Wrappers.lambdaQuery(SecurityRuleDO.class)
            .eq(SecurityRuleDO::getGroupId, groupId));
        init();
    }

    public SecurityGroupUpdateEnableStatueRes updateGroupEnableStatueReq(SecurityGroupUpdateEnableStatueReq req) {
        SecurityGroupDO groupDO = securityGroupMapper.selectById(req.getId());
        ParamCheckUtil.checkNotNull(groupDO, ExceptionConstant.SECURITY_GROUP_NOT_EXIST);

        securityGroupMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        init();
        return new SecurityGroupUpdateEnableStatueRes();
    }

    public PageInfo<SecurityRuleListRes> rulePage(PageQuery pageQuery, SecurityRuleListReq req) {
        if (StringUtils.isNotEmpty(req.getName())) {
            //在应用层处理，否则sqlite不支持
            req.setName("%" + req.getName() + "%");
        }
        if (StringUtils.isNotEmpty(req.getDescription())) {
            //在应用层处理，否则sqlite不支持
            req.setDescription("%" + req.getDescription() + "%");
        }
        Page<SecurityRuleDO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        List<SecurityRuleDO> list = securityRuleMapper.selectByCondition(page, req);
        if (CollectionUtils.isEmpty(list)) {
            return PageInfo.of(null, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        List<SecurityRuleListRes> respList = list.stream().map(SecurityRuleDO::toListRes).collect(Collectors.toList());
        return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public List<SecurityRuleListRes> ruleList(SecurityRuleListReq req) {
        List<SecurityRuleDO> list = securityRuleMapper.selectList(new LambdaQueryWrapper<SecurityRuleDO>()
                .eq(null != req.getGroupId(), SecurityRuleDO::getGroupId, req.getGroupId())
        );
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(SecurityRuleDO::toListRes).collect(Collectors.toList());
    }

    public void createRule(SecurityRuleCreateReq req) {
        SecurityRuleDO ruleDO = new SecurityRuleDO();
        BeanUtil.copyProperties(req, ruleDO);
        ruleDO.setUserId(SystemContextHolder.getUserId())
                .setCreateTime(new Date())
                .setEnable(EnableStatusEnum.ENABLE.getStatus())
                .setUpdateTime(new Date());
        securityRuleMapper.insert(ruleDO);
        clearCache();
    }

    public void updateRule(SecurityRuleUpdateReq req) {
        SecurityRuleDO ruleDO = securityRuleMapper.selectById(req.getId());
        BeanUtil.copyProperties(req, ruleDO);
        securityRuleMapper.updateById(ruleDO);
        clearCache();
    }

    public void deleteRule(Integer ruleId) {
        securityRuleMapper.deleteById(ruleId);
        clearCache();
    }

    public SecurityRuleUpdateEnableStatueRes updateRuleEnableStatueReq(SecurityRuleUpdateEnableStatueReq req) {
        SecurityRuleDO ruleDO = securityRuleMapper.selectById(req.getId());
        ParamCheckUtil.checkNotNull(ruleDO, ExceptionConstant.SECURITY_RULE_NOT_EXIST);

        securityRuleMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        clearCache();
        return new SecurityRuleUpdateEnableStatueRes();
    }

    /**
     * 判断ip在该安全组下是否允许,如果安全组没有创建，则放行，默认黑名单规则
     * @param ip 被判断的IP地址
     * @param groupId 安全组Id
     * @return 是否放行
     */
    public boolean judgeAllow(String ip, Integer groupId) {
        ip = ip.toLowerCase();
        // 不能判断当前连接的IP，保守处理，拒绝放行
        if (StrUtil.isEmpty(ip)) {
            log.debug("[SecurityGroup] cannot get remote ip,this pack be reject");
            return false;
        }

        // 黑名单规则，没有该安全组，则放行
        if (groupId == null) {
            return true;
        }
        SecurityGroupDO groupDO = securityGroupMap.get(groupId);
        if (groupDO == null) {
            return true;
        }

        Boolean allow = null;
        String judgeAllowMapKey = ip + groupId;
        if (ipAllowControlCache.containsKey(judgeAllowMapKey)) {
            allow = ipAllowControlCache.get(judgeAllowMapKey);
            log.debug("[SecurityGroup] ip:{} groupId:{} cached security strategy:{}", ip, groupId, allow ? "allow" : "reject");
            return allow;
        }

        List<SecurityRuleDO>  ruleDOList = securityRuleMapper.selectList(Wrappers.lambdaQuery(SecurityRuleDO.class)
            .eq(SecurityRuleDO::getGroupId, groupId)
            .eq(SecurityRuleDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
            .orderByAsc(SecurityRuleDO::getPriority)
        );
        for (SecurityRuleDO ruleDO : ruleDOList) {
            SecurityRulePassTypeEnum passType = ruleDO.judge(ip);
            if (passType == SecurityRulePassTypeEnum.ALLOW) {
                allow = true;
                log.debug("[SecurityGroup] ip:{} groupId:{} ruleId:{} security strategy:{}", ip, groupId, ruleDO.getId(), "allow");
                break;
            }
            if (passType == SecurityRulePassTypeEnum.DENY) {
                allow = false;
                log.info("[SecurityGroup] ip:{} groupId:{} ruleId:{} security strategy:{}", ip, groupId, ruleDO.getId(), "reject");
                break;
            }
        }

        // 当前IP没有匹配到任何一条规则，则使用安全组默认规则
        if (allow == null) {
            allow = SecurityRulePassTypeEnum.ALLOW.getType().equals(groupDO.getDefaultPassType());
            log.debug("[SecurityGroup] ip:{} groupId{} use security group default strategy:{}", ip, groupId, allow ? "allow" : "reject");
        }

        ipAllowControlCache.put(judgeAllowMapKey, allow);

        return allow;
    }
}
