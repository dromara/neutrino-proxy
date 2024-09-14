package org.dromara.neutrinoproxy.server.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.dromara.neutrinoproxy.server.controller.res.proxy.PortMappingCreateRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.PortMappingDetailRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.PortMappingListRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.PortMappingUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.dal.*;
import org.dromara.neutrinoproxy.server.dal.entity.*;
import org.dromara.neutrinoproxy.server.service.bo.FlowLimitBO;
import org.dromara.neutrinoproxy.server.service.bo.FullDomainNameBO;
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
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Component
public class PortMappingService implements LifecycleBean {
    @Db
    private PortMappingMapper portMappingMapper;
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private UserMapper userMapper;
    @Db
    private PortPoolMapper portPoolMapper;
    @Db
    private DomainMapper domainMapper;
    @Db
    private DomainPortMappingMapper domainPortMappingMapper;
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
    private final Map<Integer, Integer> mappingPortToSecurityGroupMap = new ConcurrentHashMap<>();
    private final Cache<Integer, Integer> serverPortToPortMappingIdCache = CacheUtil.newLRUCache(500, 1000 * 60 * 10);
    // 端口映射id到licenseId
    private final Cache<Integer, Integer> idToLicenseIdCache = CacheUtil.newLRUCache(500, 1000 * 60 * 10);
    // 流量限制缓存
    private final Cache<Integer, FlowLimitBO> flowLimitCache = CacheUtil.newLRUCache(500, 1000 * 60 * 5);

    public PageInfo<PortMappingListRes> page(PageQuery pageQuery, PortMappingListReq req) {
        if (StringUtils.isNotEmpty(req.getDescription())) {
            //描述字段为模糊查询，在应用层处理，否则sqlite不支持
            req.setDescription("%" + req.getDescription() + "%");
        }

        // 协议名称转换
        if (StringUtils.isNotBlank(req.getProtocal())) {
            NetworkProtocolEnum networkProtocolEnum = NetworkProtocolEnum.of(req.getProtocal());
            req.setProtocal(networkProtocolEnum.getDesc());
        }

        Page<PortMappingDO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        List<PortMappingDO> list = portMappingMapper.selectPortMappingByCondition(page, req);
        List<PortMappingListRes> respList = list.stream().map(PortMappingDO::toRes).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }

        Set<Integer> licenseIds = respList.stream().map(PortMappingListRes::getLicenseId).collect(Collectors.toSet());
        List<LicenseDO> licenseList = licenseMapper.findByIds(licenseIds);
        if (CollectionUtil.isEmpty(licenseList)) {
            return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        Set<Integer> userIds = licenseList.stream().map(LicenseDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userList = userMapper.findByIds(userIds);
        Map<Integer, LicenseDO> licenseMap = licenseList.stream().collect(Collectors.toMap(LicenseDO::getId, Function.identity()));
        Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));

        //域名相关
        Set<Integer> portMappingIds = respList.stream().map(PortMappingListRes::getId).collect(Collectors.toSet());
        List<FullDomainNameBO> fullDomainNameBOS = domainMapper.selectFullDomainNameListByPortMappingIds(portMappingIds);
        Map<Integer, List<FullDomainNameBO>> fullDomainNameBOMap = fullDomainNameBOS.stream().collect(Collectors.groupingBy(FullDomainNameBO::getPortMappingId));

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
            //域名映射
            item.setFullDomainMappings(fullDomainNameBOMap.get(item.getId()));
            if (NetworkProtocolEnum.HTTP.getDesc().equals(item.getProtocal())) {
                item.setProtocal("HTTP(S)");
            }
        });
        //sorted [userId asc] [licenseId asc] [createTime asc]
        respList = respList.stream()
            .filter(e -> null != e.getUserId())
            .sorted(Comparator.comparing(PortMappingListRes::getUserId).thenComparing(PortMappingListRes::getLicenseId).thenComparing(PortMappingListRes::getCreateTime)).collect(Collectors.toList());
        return PageInfo.of(respList, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public PortMappingCreateRes create(PortMappingCreateReq req) {
        LicenseDO licenseDO = licenseMapper.findById(req.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }
        PortPoolDO portPoolDO = portPoolMapper.findByPort(req.getServerPort());
        ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
        ParamCheckUtil.checkExpression(null == portMappingMapper.findByPort(req.getServerPort(), null), ExceptionConstant.PORT_CANNOT_REPEAT_MAPPING, req.getServerPort());
        //验证域名映射相关参数条件
        if (NetworkProtocolEnum.isHttp(req.getProtocal()) && CollectionUtil.isNotEmpty(req.getDomainMappings())) {
            Set<Integer> domainIds = req.getDomainMappings().stream().map(item -> item.getDomainId()).collect(Collectors.toSet());
            List<DomainNameDO> domainNameDOS = domainMapper.selectBatchIds(domainIds);
            Map<Integer, DomainNameDO> domainNameDOMap = domainNameDOS.stream().collect(Collectors.toMap(DomainNameDO::getId, Function.identity()));

            req.getDomainMappings().forEach(item -> {
                DomainNameDO domainNameDO = domainNameDOMap.get(item.getDomainId());
                ParamCheckUtil.checkNotNull(domainNameDO, ExceptionConstant.DOMAIN_NAME_NOT_EXIST);
                //检查当前域名是否被禁用
                //ParamCheckUtil.checkExpression(Objects.equals(EnableStatusEnum.ENABLE.getStatus(), domainNameDO.getEnable()), ExceptionConstant.DOMAIN_NAME_IS_DISABLE, domainNameDO.getDomain());
                //检查子域名是否重复
                ParamCheckUtil.checkExpression(!domainPortMappingMapper.checkRepeatBySubdomain(item.getSubdomain(), item.getDomainId(), null), ExceptionConstant.SUDOMAIN_NAME_CANNOT_REPEAT);
            });
        }
        Date now = new Date();
        PortMappingDO portMappingDO = new PortMappingDO();
        portMappingDO.setLicenseId(req.getLicenseId());
        portMappingDO.setProtocal(req.getProtocal());
        portMappingDO.setServerPort(req.getServerPort());
        portMappingDO.setClientIp(req.getClientIp());
        portMappingDO.setClientPort(req.getClientPort());
        portMappingDO.setUpLimitRate(req.getUpLimitRate());
        portMappingDO.setDownLimitRate(req.getDownLimitRate());
        portMappingDO.setProxyResponses(req.getProxyResponses());
        portMappingDO.setProxyTimeoutMs(req.getProxyTimeoutMs());
        portMappingDO.setDescription(req.getDescription());
        portMappingDO.setIsOnline(OnlineStatusEnum.OFFLINE.getStatus());
        portMappingDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
        portMappingDO.setCreateTime(now);
        portMappingDO.setUpdateTime(now);
        portMappingMapper.insert(portMappingDO);
        // 更新VisitorChannel
        visitorChannelService.addVisitorChannelByPortMapping(portMappingDO);

        // 更新域名映射，添加新的域名映射
        if (NetworkProtocolEnum.isHttp(portMappingDO.getProtocal()) && CollectionUtil.isNotEmpty(req.getDomainMappings())) {
            Set<Integer> domainIds = req.getDomainMappings().stream().map(item -> item.getDomainId()).collect(Collectors.toSet());
            List<DomainNameDO> domainNameDOS = domainMapper.selectBatchIds(domainIds);
            Map<Integer, DomainNameDO> domainNameDOMap = domainNameDOS.stream().collect(Collectors.toMap(DomainNameDO::getId, Function.identity()));

            req.getDomainMappings().forEach(item -> {
                DomainNameDO domainNameDO = domainNameDOMap.get(item.getDomainId());
                //创建域名映射
                DomainPortMappingDO domainPortMappingDO = new DomainPortMappingDO();
                domainPortMappingDO.setDomainNameId(item.getDomainId());
                domainPortMappingDO.setSubdomain(item.getSubdomain());
                domainPortMappingDO.setPortMappingId(portMappingDO.getId());
                domainPortMappingMapper.insert(domainPortMappingDO);
                //设置完整域名到服务端端口的映射
                ProxyUtil.setFullDomainToServerPort(StrUtil.join(StrPool.DOT, item.getSubdomain(), domainNameDO.getDomain()), portMappingDO.getServerPort());
            });
        }

        updateMappingPortToSecurityGroupMap(portMappingDO.getServerPort(), req.getSecurityGroupId());

        // 更新端口到映射的缓存
        serverPortToPortMappingIdCache.put(req.getServerPort(), portMappingDO.getId());
        // 更新端口映射到licenseId的缓存
        idToLicenseIdCache.put(portMappingDO.getId(), portMappingDO.getLicenseId());
        // 刷新流量限制缓存
        refreshFlowLimitCache(portMappingDO.getId(), portMappingDO.getUpLimitRate(), portMappingDO.getDownLimitRate());

        return new PortMappingCreateRes();
    }

    public void update(PortMappingUpdateReq req) {
        LicenseDO licenseDO = licenseMapper.findById(req.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }
        PortPoolDO portPoolDO = portPoolMapper.findByPort(req.getServerPort());
        ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
        ParamCheckUtil.checkExpression(null == portMappingMapper.findByPort(req.getServerPort(), Sets.newHashSet(req.getId())), ExceptionConstant.PORT_CANNOT_REPEAT_MAPPING, req.getServerPort());
        // 查询原端口映射
        PortMappingDO oldPortMappingDO = portMappingMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(oldPortMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

        //验证域名映射相关参数条件
        if (NetworkProtocolEnum.isHttp(req.getProtocal()) && CollectionUtil.isNotEmpty(req.getDomainMappings())) {
            Set<Integer> domainIds = req.getDomainMappings().stream().map(item -> item.getDomainId()).collect(Collectors.toSet());
            List<DomainNameDO> domainNameDOS = domainMapper.selectBatchIds(domainIds);
            Map<Integer, DomainNameDO> domainNameDOMap = domainNameDOS.stream().collect(Collectors.toMap(DomainNameDO::getId, Function.identity()));

            req.getDomainMappings().forEach(item -> {
                DomainNameDO domainNameDO = domainNameDOMap.get(item.getDomainId());
                ParamCheckUtil.checkNotNull(domainNameDO, ExceptionConstant.DOMAIN_NAME_NOT_EXIST);
                //检查子域名是否重复
                ParamCheckUtil.checkExpression(!domainPortMappingMapper.checkRepeatBySubdomain(item.getSubdomain(), item.getDomainId(), req.getId()),
                    ExceptionConstant.SUDOMAIN_NAME_CANNOT_REPEAT);
            });
        }
        // 更新端口映射
        portMappingMapper.update(null, new LambdaUpdateWrapper<PortMappingDO>()
            .eq(PortMappingDO::getId, req.getId())
            .set(PortMappingDO::getProtocal, req.getProtocal())
            .set(PortMappingDO::getServerPort, req.getServerPort())
            .set(PortMappingDO::getClientIp, req.getClientIp())
            .set(PortMappingDO::getClientPort, req.getClientPort())
            .set(PortMappingDO::getUpLimitRate, req.getUpLimitRate())
            .set(PortMappingDO::getDownLimitRate, req.getDownLimitRate())
            .set(PortMappingDO::getProxyTimeoutMs, req.getProxyTimeoutMs())
            .set(PortMappingDO::getProxyResponses, req.getProxyResponses())
            .set(PortMappingDO::getSecurityGroupId, req.getSecurityGroupId())
            .set(PortMappingDO::getDescription, req.getDescription())
            .set(PortMappingDO::getUpdateTime, new Date())
        );

        // 更新VisitorChannel
        PortMappingDO portMappingDO = portMappingMapper.findById(req.getId());
        visitorChannelService.updateVisitorChannelByPortMapping(oldPortMappingDO, portMappingDO);

        // 更新域名映射
        // 删除老的域名映射
        if (NetworkProtocolEnum.isHttp(oldPortMappingDO.getProtocal())) {
            //删除完整域名到服务端端口的映射
            ProxyUtil.removeFullDomainToServerPortByServerPort(oldPortMappingDO.getServerPort());
            //删除旧的域名映射
            LambdaQueryWrapper<DomainPortMappingDO> lambdaQueryWrapper = Wrappers.<DomainPortMappingDO>lambdaQuery()
                .eq(DomainPortMappingDO::getPortMappingId, oldPortMappingDO.getId());
            domainPortMappingMapper.delete(lambdaQueryWrapper);
        }
        // 添加新的域名映射
        if (NetworkProtocolEnum.isHttp(portMappingDO.getProtocal()) && CollectionUtil.isNotEmpty(req.getDomainMappings())) {
            Set<Integer> domainIds = req.getDomainMappings().stream().map(item -> item.getDomainId()).collect(Collectors.toSet());
            List<DomainNameDO> domainNameDOS = domainMapper.selectBatchIds(domainIds);
            Map<Integer, DomainNameDO> domainNameDOMap = domainNameDOS.stream().collect(Collectors.toMap(DomainNameDO::getId, Function.identity()));

            req.getDomainMappings().forEach(item -> {
                DomainNameDO domainNameDO = domainNameDOMap.get(item.getDomainId());
                //创建域名映射
                DomainPortMappingDO domainPortMappingDO = new DomainPortMappingDO();
                domainPortMappingDO.setDomainNameId(item.getDomainId());
                domainPortMappingDO.setSubdomain(item.getSubdomain());
                domainPortMappingDO.setPortMappingId(portMappingDO.getId());
                domainPortMappingMapper.insert(domainPortMappingDO);
                //设置完整域名到服务端端口的映射
                ProxyUtil.setFullDomainToServerPort(StrUtil.join(StrPool.DOT, item.getSubdomain(), domainNameDO.getDomain()), portMappingDO.getServerPort());
            });
        }

        updateMappingPortToSecurityGroupMap(portMappingDO.getServerPort(), req.getSecurityGroupId());

        // 更新端口到映射的缓存
        serverPortToPortMappingIdCache.put(req.getServerPort(), req.getId());
        // 更新端口映射到licenseId的缓存
        idToLicenseIdCache.put(portMappingDO.getId(), portMappingDO.getLicenseId());
        // 刷新流量限制缓存
        refreshFlowLimitCache(req.getId(), req.getUpLimitRate(), req.getDownLimitRate());
    }

    public PortMappingDetailRes detail(Integer id) {
        PortMappingDO portMappingDO = portMappingMapper.findById(id);
        if (null == portMappingDO) {
            return null;
        }
        PortMappingDetailRes res = new PortMappingDetailRes()
            .setId(portMappingDO.getId())
            .setLicenseId(portMappingDO.getLicenseId())
            .setServerPort(portMappingDO.getServerPort())
            .setClientIp(portMappingDO.getClientIp())
            .setClientPort(portMappingDO.getClientPort())
            .setIsOnline(portMappingDO.getIsOnline())
            .setProxyTimeoutMs(portMappingDO.getProxyTimeoutMs())
            .setProxyResponses(portMappingDO.getProxyResponses())
            .setEnable(portMappingDO.getEnable())
            .setCreateTime(portMappingDO.getCreateTime())
            .setUpdateTime(portMappingDO.getUpdateTime());

        LicenseDO license = licenseMapper.findById(portMappingDO.getLicenseId());
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

    public PortMappingUpdateEnableStatusRes updateEnableStatus(PortMappingUpdateEnableStatusReq req) {
        PortMappingDO portMappingDO = portMappingMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(portMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

        LicenseDO licenseDO = licenseMapper.findById(portMappingDO.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }

        portMappingMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());

        // 更新VisitorChannel
        portMappingDO.setEnable(req.getEnable());
        if (EnableStatusEnum.ENABLE == EnableStatusEnum.of(req.getEnable())) {
            visitorChannelService.addVisitorChannelByPortMapping(portMappingDO);
        } else {
            visitorChannelService.removeVisitorChannelByPortMapping(portMappingDO);
        }

        return new PortMappingUpdateEnableStatusRes();
    }

    public void delete(Integer id) {
        PortMappingDO portMappingDO = portMappingMapper.findById(id);
        ParamCheckUtil.checkNotNull(portMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

        LicenseDO licenseDO = licenseMapper.findById(portMappingDO.getLicenseId());
        if (null != licenseDO && !SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }

        portMappingMapper.deleteById(id);

        // 更新VisitorChannel
        visitorChannelService.removeVisitorChannelByPortMapping(portMappingDO);

        // 删除域名映射
        if (NetworkProtocolEnum.isHttp(portMappingDO.getProtocal())) {
            //删除完整域名到服务端端口的映射
            ProxyUtil.removeFullDomainToServerPortByServerPort(portMappingDO.getServerPort());
            //删除旧的域名映射
            LambdaQueryWrapper<DomainPortMappingDO> lambdaQueryWrapper = Wrappers.<DomainPortMappingDO>lambdaQuery()
                .eq(DomainPortMappingDO::getPortMappingId, portMappingDO.getId());
            domainPortMappingMapper.delete(lambdaQueryWrapper);
        }

        updateMappingPortToSecurityGroupMap(portMappingDO.getServerPort(), null);

        // 删除id到licenseId的映射
        idToLicenseIdCache.remove(id);
        // 删除流量限制缓存
        flowLimitCache.remove(id);
    }

    public void portBindSecurityGroup(Integer portMappingId, Integer groupId) {
        PortMappingDO mappingDO = portMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(groupId);
        mappingDO.setUpdateTime(new Date());
        portMappingMapper.updateById(mappingDO);
        updateMappingPortToSecurityGroupMap(mappingDO.getServerPort(), groupId);
    }

    public void portUnbindSecurityGroup(Integer portMappingId) {
        PortMappingDO mappingDO = portMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(0);
        mappingDO.setUpdateTime(new Date());
        portMappingMapper.updateById(mappingDO);
        updateMappingPortToSecurityGroupMap(mappingDO.getServerPort(), null);
    }

    /**
     * 根据license查询可用的端口映射列表
     *
     * @param licenseId
     * @return
     */
    public List<PortMappingDO> findEnableListByLicenseId(Integer licenseId) {
        return portMappingMapper.findEnableListByLicenseId(licenseId);
    }

    public Integer getSecurityGroupIdByMappingPort(Integer port) {
        return mappingPortToSecurityGroupMap.get(port);
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
        portMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());

        List<PortMappingDO> allMappingDOList = portMappingMapper.selectList(Wrappers.lambdaQuery(PortMappingDO.class));
        allMappingDOList.forEach(item -> {
            Integer securityGroupId = item.getSecurityGroupId();
            if (securityGroupId != null && securityGroupId > 0) {
                updateMappingPortToSecurityGroupMap(item.getServerPort(), item.getSecurityGroupId());
            }
            // 更新端口到映射的缓存
            serverPortToPortMappingIdCache.put(item.getServerPort(), item.getId());
            // 更新端口映射到licenseId的缓存
            idToLicenseIdCache.put(item.getId(), item.getLicenseId());
            // 刷新流量限制缓存
            refreshFlowLimitCache(item.getId(), item.getUpLimitRate(), item.getDownLimitRate());
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
            PortMappingDO portMappingDO = portMappingMapper.findById(id);
            if (null != portMappingDO) {
                refreshFlowLimitCache(id, portMappingDO.getUpLimitRate(), portMappingDO.getDownLimitRate());
                res = flowLimitCache.get(id);
            }
        }
        return res;
    }

    public Integer getPortMappingIdByServerPort(Integer serverPort) {
        if (null == serverPort) {
            return null;
        }
        Integer id = serverPortToPortMappingIdCache.get(serverPort);
        if (null != id) {
            return id;
        }
        List<PortMappingDO> portMappingDOList = portMappingMapper.findListByServerPort(serverPort);
        // 不存在 或者 有多条记录，都不处理
        if (CollectionUtils.isEmpty(portMappingDOList) || portMappingDOList.size() > 1) {
            return null;
        }
        id = portMappingDOList.get(0).getId();
        serverPortToPortMappingIdCache.put(serverPort, id);
        return id;
    }

    public Integer getLicenseIdById(Integer id) {
        Integer licenseId = idToLicenseIdCache.get(id);
        if (null == licenseId) {
            PortMappingDO portMappingDO = portMappingMapper.findById(id);
            if (null != portMappingDO) {
                licenseId = portMappingDO.getLicenseId();
                idToLicenseIdCache.put(id, licenseId);
            }
        }
        return licenseId;
    }

    public FlowLimitBO getFlowLimitByServerPort(Integer serverPort) {
        Integer id = getPortMappingIdByServerPort(serverPort);
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

    private void updateMappingPortToSecurityGroupMap(Integer serverPort, Integer securityGroupId) {
        if (securityGroupId == null || securityGroupId == 0) {
            mappingPortToSecurityGroupMap.remove(serverPort);
            return;
        }
        mappingPortToSecurityGroupMap.put(serverPort, securityGroupId);
    }

    @Override
    public void start() throws Throwable {

    }

    /**
     * 服务端项目停止、启动时，更新在线状态为离线
     */
    @Override
    public void stop() throws Throwable {
        portMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
    }
}
