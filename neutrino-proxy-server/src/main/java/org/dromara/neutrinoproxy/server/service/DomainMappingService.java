package org.dromara.neutrinoproxy.server.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.dromara.neutrinoproxy.server.proxy.domain.DomainMapping;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyMapping;
import org.dromara.neutrinoproxy.server.service.bo.FlowLimitBO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.util.PortAvailableUtil;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.dromara.neutrinoproxy.server.util.StringUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.data.annotation.Tran;

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

    @Tran
    public PortMappingCreateRes modify(DomainMappingDO dmd) {
        LicenseDO licenseDO = licenseMapper.findById(dmd.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }
        if (null != dmd.getId()) { // 编辑
            ParamCheckUtil.checkExpression(!domainMappingMapper.checkRepeatByDomain(dmd.getDomain(), new HashSet<>(){{add(dmd.getId());}}), ExceptionConstant.DONAME_CONNOT_REPEAT);

            Date now = new Date();
            dmd.setEnable(EnableStatusEnum.ENABLE.getStatus());
            dmd.setUpdateTime(now);
            domainMappingMapper.updateById(dmd);
        } else { // 新增
            ParamCheckUtil.checkExpression(!domainMappingMapper.checkRepeatByDomain(dmd.getDomain(), null), ExceptionConstant.DONAME_CONNOT_REPEAT);

            Date now = new Date();
            dmd.setEnable(EnableStatusEnum.ENABLE.getStatus());
            dmd.setCreateTime(now);
            dmd.setUpdateTime(now);
            domainMappingMapper.insert(dmd);
        }
        // 更新VisitorChannel
        visitorChannelService.addVisitorChannelByProxyMapping(new ProxyMapping(dmd.getLicenseId(),dmd.getId(),dmd.getTargetPath(), NetworkProtocolEnum.HTTP.getDesc()));
        // 更新域名映射
        ProxyUtil.domainMapingMap.put(dmd.getDomain(), new DomainMapping(dmd.getId(), dmd.getLicenseId(), dmd.getDomain(), dmd.getTargetPath()));

        return new PortMappingCreateRes();
    }
    @Tran
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
            visitorChannelService.addVisitorChannelByProxyMapping(new ProxyMapping(domainMappingDO.getLicenseId(),domainMappingDO.getId(),domainMappingDO.getTargetPath(), NetworkProtocolEnum.HTTP.getDesc()));
        } else {
            visitorChannelService.removeVisitorChannelByProxyMapping(new ProxyMapping(domainMappingDO.getLicenseId(),domainMappingDO.getId(),domainMappingDO.getTargetPath(), NetworkProtocolEnum.HTTP.getDesc()));
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

    }

    public void domainBindSecurityGroup(Integer portMappingId, Integer groupId) {
        DomainMappingDO mappingDO = domainMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(groupId);
        mappingDO.setUpdateTime(new Date());
        domainMappingMapper.updateById(mappingDO);
    }

    public void domainUnbindSecurityGroup(Integer portMappingId) {
        DomainMappingDO mappingDO = domainMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(0);
        mappingDO.setUpdateTime(new Date());
        domainMappingMapper.updateById(mappingDO);
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


    /**
     * 服务端项目停止、启动时，更新在线状态为离线
     */
    @Init
    public void init() {
        // aot 阶段，不初始化
        if (NativeDetector.isAotRuntime()) {
            return;
        }
        List<DomainMappingDO> allMappingDOList = domainMappingMapper.selectList(Wrappers.lambdaQuery(DomainMappingDO.class));
        if(CollectionUtil.isNotEmpty(allMappingDOList)) {
            allMappingDOList.forEach(domain -> {
                ProxyUtil.domainMapingMap.put(domain.getDomain(),
                    new DomainMapping(domain.getId(), domain.getLicenseId(), domain.getDomain(), domain.getTargetPath()));
            });
        }
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

    /**
     * 检查端口是否被占用
     * 端口映射编辑时，如果端口号没有变动，则不验证。避免出现端口映射正在使用时，无法更新端口映射其他信息的问题
     * @param domain
     * @return
     */
    public boolean exitDomain(String domain, Integer id) {
        DomainMappingDO domainMappingDO = domainMappingMapper.findByDomain(domain, ObjectUtil.isNotNull(id) ? new HashSet<>(){{add(id);}} : new HashSet());
        if (null == domainMappingDO) return Boolean.TRUE;
        return Boolean.FALSE;
    }
}
