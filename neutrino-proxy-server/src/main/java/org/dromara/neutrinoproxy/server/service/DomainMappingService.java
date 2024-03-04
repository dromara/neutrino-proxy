package org.dromara.neutrinoproxy.server.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import com.google.common.collect.Sets;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.db.DBInitialize;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingCreateReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingListReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingUpdateEnableStatusReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingUpdateReq;
import org.dromara.neutrinoproxy.server.controller.res.proxy.*;
import org.dromara.neutrinoproxy.server.dal.DomainMappingMapper;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortPoolMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.*;
import org.dromara.neutrinoproxy.server.service.bo.FlowLimitBO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.dromara.neutrinoproxy.server.util.StringUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.runtime.NativeDetector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 *  域名解析 service
 *  author xiaojie
 */
@Component
public class DomainMappingService implements LifecycleBean {
    @Db
    private DomainMappingMapper domainMappingMapper;
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private UserMapper userMapper;
    @Db
    private PortPoolMapper portPoolMapper;
    @Inject
    private VisitorChannelService visitorChannelService;

    @Inject
    private PortPoolService portPoolService;
    @Inject
    private ProxyConfig proxyConfig;
    @Inject
    private DBInitialize dbInitialize;
    @Inject
    private LicenseService licenseService;

    /** 端口到安全组Id的映射 */
    private final Map<String, Integer> mappingDomainToSecurityGroupMap = new ConcurrentHashMap<>();
    /**
     * 域名 到 域名解析id的映射
     */
    private final Cache<String, Integer> domainToDomainMappingIdCache = CacheUtil.newLRUCache(500, 1000 * 60 * 10);
    // 域名解析id到licenseId
    private final Cache<Integer, Integer> idToLicenseIdCache = CacheUtil.newLRUCache(500, 1000 * 60 * 10);
    // 流量限制缓存
    private final Cache<Integer, FlowLimitBO> flowLimitCache = CacheUtil.newLRUCache(500, 1000 * 60 * 5);

    public PageInfo<DomainMappingDto> page(PageQuery pageQuery, DomainMappingDto req) {
        if (StringUtils.isNotEmpty(req.getDescription())) {
            //描述字段为模糊查询，在应用层处理，否则sqlite不支持
            req.setDescription("%" + req.getDescription() + "%");
        }

        // 协议名称转换
        if (StringUtils.isNotBlank(req.getProtocal())) {
            NetworkProtocolEnum networkProtocolEnum = NetworkProtocolEnum.of(req.getProtocal());
            req.setProtocal(networkProtocolEnum.getDesc());
        }

        Page<DomainMappingDO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        List<DomainMappingDO> list = domainMappingMapper.selectDomainMappingByCondition(page, req);
        List<DomainMappingDto> respList = list.stream().map(DomainMappingDO::toRes).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }

        Set<Integer> licenseIds = respList.stream().map(DomainMappingDto::getLicenseId).collect(Collectors.toSet());
        List<LicenseDO> licenseList = licenseMapper.findByIds(licenseIds);
        if (CollectionUtil.isEmpty(licenseList)) {
            return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        Set<Integer> userIds = licenseList.stream().map(LicenseDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userList = userMapper.findByIds(userIds);
        Map<Integer, LicenseDO> licenseMap = licenseList.stream().collect(Collectors.toMap(LicenseDO::getId, Function.identity()));
        Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));

        respList.forEach(item -> {
            LicenseDO license = licenseMap.get(item.getLicenseId());
            if (null == license) {
                return;
            }
            item.setLicenseName(license.getName());
            item.setUserId(license.getUserId());
            UserDO user = userMap.get(license.getUserId());
            if (null == user) {
                return;
            }
            item.setUserName(user.getName());
//            if (StrUtil.isNotBlank(proxyConfig.getServer().getTcp().getDomainName()) && StrUtil.isNotBlank(item.getDomain())) {
//                item.setDomain(item.getDomain() + "." + proxyConfig.getServer().getTcp().getDomainName());
//            }
            if (NetworkProtocolEnum.HTTP.getDesc().equals(item.getProtocal())) {
                item.setProtocal("HTTP(S)");
            }
        });
        //sorted [userId asc] [licenseId asc] [createTime asc]
        respList = respList.stream()
            .filter(e -> null != e.getUserId())
            .sorted(Comparator.comparing(DomainMappingDto::getUserId).thenComparing(DomainMappingDto::getLicenseId).thenComparing(DomainMappingDto::getCreateTime)).collect(Collectors.toList());
        return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public PortMappingCreateRes modify(DomainMappingDto req) {
        LicenseDO licenseDO = licenseMapper.findById(req.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }
//        PortPoolDO portPoolDO = portPoolMapper.findByPort(req.getDomain());
//        ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
        ParamCheckUtil.checkExpression(null == domainMappingMapper.findByDomain(req.getDomain(), null), ExceptionConstant.PORT_CANNOT_REPEAT_MAPPING, req.getDomain());
        ParamCheckUtil.checkExpression(!domainMappingMapper.checkRepeatByDomain(req.getDomain(), null), ExceptionConstant.PORT_MAPPING_SUBDONAME_CONNOT_REPEAT);

        Date now = new Date();
        DomainMappingDO domainMappingDO = (DomainMappingDO) req;
        domainMappingDO.setIsOnline(OnlineStatusEnum.OFFLINE.getStatus());
        domainMappingDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
        domainMappingDO.setCreateTime(now);
        domainMappingDO.setUpdateTime(now);
        domainMappingMapper.insert(domainMappingDO);
        // 更新VisitorChannel
//        visitorChannelService.addVisitorChannelByPortMapping(domainMappingDO);
        // 更新域名映射
        if (NetworkProtocolEnum.isHttp(domainMappingDO.getProtocal()) && StrUtil.isNotBlank(proxyConfig.getServer().getTcp().getDomainName()) && StrUtil.isNotBlank(domainMappingDO.getDomain())) {
//            ProxyUtil.setSubdomainToServerPort(domainMappingDO.getDomain(), domainMappingDO.getServerPort());
        }

        updateMappingDomainToSecurityGroupMap(domainMappingDO.getDomain(), req.getSecurityGroupId());

        // 更新端口到映射的缓存
        domainToDomainMappingIdCache.put(req.getDomain(), domainMappingDO.getId());
        // 更新端口映射到licenseId的缓存
        idToLicenseIdCache.put(domainMappingDO.getId(), domainMappingDO.getLicenseId());
        // 刷新流量限制缓存
        refreshFlowLimitCache(domainMappingDO.getId(), domainMappingDO.getUpLimitRate(), domainMappingDO.getDownLimitRate());

        return new PortMappingCreateRes();
    }

    public PortMappingUpdateEnableStatusRes updateEnableStatus(PortMappingUpdateEnableStatusReq req) {
        DomainMappingDO domainMappingDO = domainMappingMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(domainMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

        LicenseDO licenseDO = licenseMapper.findById(domainMappingDO.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }

        domainMappingMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());

        // 更新VisitorChannel
        domainMappingDO.setEnable(req.getEnable());
        if (EnableStatusEnum.ENABLE == EnableStatusEnum.of(req.getEnable())) {
//            visitorChannelService.addVisitorChannelByPortMapping(domainMappingDO);
        } else {
//            visitorChannelService.removeVisitorChannelByPortMapping(domainMappingDO);
        }

        return new PortMappingUpdateEnableStatusRes();
    }

    public void delete(Integer id) {
        DomainMappingDO domainMappingDO = domainMappingMapper.findById(id);
        ParamCheckUtil.checkNotNull(domainMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

        LicenseDO licenseDO = licenseMapper.findById(domainMappingDO.getLicenseId());
        if (null != licenseDO && !SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }

        domainMappingMapper.deleteById(id);

        // 更新VisitorChannel
//        visitorChannelService.removeVisitorChannelByPortMapping(domainMappingDO);

        // 更新域名映射
        if (NetworkProtocolEnum.isHttp(domainMappingDO.getProtocal()) && StrUtil.isNotBlank(domainMappingDO.getDomain())) {
            ProxyUtil.removeSubdomainToServerPort(domainMappingDO.getDomain());
        }

        updateMappingDomainToSecurityGroupMap(domainMappingDO.getDomain(), null);

        // 删除id到licenseId的映射
        idToLicenseIdCache.remove(id);
        // 删除流量限制缓存
        flowLimitCache.remove(id);
    }

    public void domainBindSecurityGroup(Integer portMappingId, Integer groupId) {
        DomainMappingDO mappingDO = domainMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(groupId);
        mappingDO.setUpdateTime(new Date());
        domainMappingMapper.updateById(mappingDO);
        updateMappingDomainToSecurityGroupMap(mappingDO.getDomain(), groupId);
    }

    public void domainUnbindSecurityGroup(Integer portMappingId) {
        DomainMappingDO mappingDO = domainMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(0);
        mappingDO.setUpdateTime(new Date());
        domainMappingMapper.updateById(mappingDO);
        updateMappingDomainToSecurityGroupMap(mappingDO.getDomain(), null);
    }

    /**
     * 根据license查询可用的端口映射列表
     *
     * @param licenseId
     * @return
     */
    public List<DomainMappingDO> findEnableListByLicenseId(Integer licenseId) {
        return domainMappingMapper.findEnableListByLicenseId(licenseId);
    }

    public Integer getSecurityGroupIdByMappingPort(Integer port) {
        return mappingDomainToSecurityGroupMap.get(port);
    }


    /**
     * 服务端项目停止、启动时，更新在线状态为离线
     */
    @Init
    public void init() {
        // aot 阶段，不初始化
        if (NativeDetector.isAotRuntime()) {
            return;
        }
        // 服务刚启动，所以默认所有license都是离线状态。解决服务突然关闭，在线状态来不及更新的问题
//        domainMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());

        List<DomainMappingDO> allMappingDOList = domainMappingMapper.selectList(Wrappers.lambdaQuery(DomainMappingDO.class));
        allMappingDOList.forEach(item -> {
            Integer securityGroupId = item.getSecurityGroupId();
            if (securityGroupId != null && securityGroupId > 0) {
                updateMappingDomainToSecurityGroupMap(item.getDomain(), item.getSecurityGroupId());
            }
            // 更新端口到映射的缓存
            domainToDomainMappingIdCache.put(item.getDomain(), item.getId());
            // 更新端口映射到licenseId的缓存
            idToLicenseIdCache.put(item.getId(), item.getLicenseId());
            // 刷新流量限制缓存
            refreshFlowLimitCache(item.getId(), item.getUpLimitRate(), item.getDownLimitRate());
        });

        // 未配置域名，则不需要处理域名映射逻辑
        if (StrUtil.isBlank(proxyConfig.getServer().getTcp().getDomainName())) {
            return;
        }
        List<DomainMappingDO> portMappingDOList = allMappingDOList.stream()
            .filter(item -> NetworkProtocolEnum.HTTP.getDesc().equals(item.getProtocal()) && item.getDomain() != null)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(portMappingDOList)) {
            return;
        }
        portMappingDOList.forEach(item -> {
            if (StrUtil.isBlank(item.getDomain())) {
                return;
            }
//            ProxyUtil.setSubdomainToServerPort(item.getDomain(), item.getServerPort());

        });
    }

    /**
     * 刷新流量限制缓存
     * @param id
     * @param upLimitRate
     * @param downLimitRate
     */
    private void refreshFlowLimitCache(Integer id, String upLimitRate, String downLimitRate) {
        if (null == id) {
            return;
        }
        flowLimitCache.put(id, new FlowLimitBO()
            .setUpLimitRate(StringUtil.parseBytes(upLimitRate))
            .setDownLimitRate(StringUtil.parseBytes(downLimitRate))
        );
    }

    /**
     * 获取license的流量限制
     * @param id
     * @return
     */
    public FlowLimitBO getFlowLimit(Integer id) {
        FlowLimitBO res = flowLimitCache.get(id);
        if (null == res) {
            DomainMappingDO domainMappingDO = domainMappingMapper.findById(id);
            if (null != domainMappingDO) {
                refreshFlowLimitCache(id, domainMappingDO.getUpLimitRate(), domainMappingDO.getDownLimitRate());
                res = flowLimitCache.get(id);
            }
        }
        return res;
    }

    public Integer getDomainMappingIdByDomain(String domain) {
        Integer id = domainToDomainMappingIdCache.get(domain);
        if (null != id) {
            return id;
        }
        List<DomainMappingDO> domainMappingDOList = domainMappingMapper.findListByServerPort(domain);
        // 不存在 或者 有多条记录，都不处理
        if (CollectionUtils.isEmpty(domainMappingDOList) || domainMappingDOList.size() > 1) {
            return null;
        }
        id = domainMappingDOList.get(0).getId();
        domainToDomainMappingIdCache.put(domain, id);
        return id;
    }

    public Integer getLicenseIdById(Integer id) {
        Integer licenseId = idToLicenseIdCache.get(id);
        if (null == licenseId) {
            DomainMappingDO domainMappingDO = domainMappingMapper.findById(id);
            if (null != domainMappingDO) {
                licenseId = domainMappingDO.getLicenseId();
                idToLicenseIdCache.put(id, licenseId);
            }
        }
        return licenseId;
    }

    public FlowLimitBO getFlowLimitByServerPort(String domain) {
        Integer id = getDomainMappingIdByDomain(domain);
        if (null == id) {
            return null;
        }
        FlowLimitBO res = getFlowLimit(id);
        if (null == res || (null == res.getUpLimitRate() && null == res.getDownLimitRate())) {
            Integer licenseId = getLicenseIdById(id);
            if (null != licenseId) {
                res = licenseService.getFlowLimit(licenseId);
            }
        }
        return res;
    }

    private void updateMappingDomainToSecurityGroupMap(String domain, Integer securityGroupId) {
        if (securityGroupId == null || securityGroupId == 0) {
            mappingDomainToSecurityGroupMap.remove(domain);
            return;
        }
        mappingDomainToSecurityGroupMap.put(domain, securityGroupId);
    }

    @Override
    public void start() throws Throwable {

    }

    /**
     * 服务端项目停止、启动时，更新在线状态为离线
     */
    @Override
    public void stop() throws Throwable {
//        domainMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
    }

    public DomainMappingDto one(Integer id) {
        DomainMappingDO domainMappingDO = domainMappingMapper.findById(id);
        if (null == domainMappingDO) {
            return null;
        }
        DomainMappingDto res = (DomainMappingDto)domainMappingDO;

        LicenseDO license = licenseMapper.findById(domainMappingDO.getLicenseId());
        if (null != license) {
            res.setLicenseName(license.getName());
            res.setUserId(license.getUserId());
            UserDO user = userMapper.findById(license.getUserId());
            if (null != user) {
                res.setUserName(user.getName());
            }
        }
        return res;
    }
}
