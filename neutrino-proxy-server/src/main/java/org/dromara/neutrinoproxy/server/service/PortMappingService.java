package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import org.dromara.neutrinoproxy.server.controller.res.proxy.PortMappingUpdateRes;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.PortPoolMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortPoolDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.runtime.NativeDetector;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @Inject
    private VisitorChannelService visitorChannelService;

    @Inject
    private PortPoolService portPoolService;
    @Inject
    private ProxyConfig proxyConfig;
    @Inject
    private DBInitialize dbInitialize;

    /** 端口到安全组Id的映射 */
    private final Map<Integer, Integer> portToSecurityGroupMap = new ConcurrentHashMap<>();

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
            if (StrUtil.isNotBlank(proxyConfig.getServer().getTcp().getDomainName()) && StrUtil.isNotBlank(item.getSubdomain())) {
                item.setDomain(item.getSubdomain() + "." + proxyConfig.getServer().getTcp().getDomainName());
            }
            if (NetworkProtocolEnum.HTTP.getDesc().equals(item.getProtocal())) {
                item.setProtocal("HTTP(S)");
            }
        });
        //sorted [userId asc] [licenseId asc] [createTime asc]
        respList = respList.stream().sorted(Comparator.comparing(PortMappingListRes::getUserId).thenComparing(PortMappingListRes::getLicenseId).thenComparing(PortMappingListRes::getCreateTime)).collect(Collectors.toList());
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
        ParamCheckUtil.checkExpression(!portMappingMapper.checkRepeatBySubdomain(req.getSubdomain(), null), ExceptionConstant.PORT_MAPPING_SUBDONAME_CONNOT_REPEAT);

        Date now = new Date();
        PortMappingDO portMappingDO = new PortMappingDO();
        portMappingDO.setLicenseId(req.getLicenseId());
        portMappingDO.setProtocal(req.getProtocal());
        portMappingDO.setSubdomain(req.getSubdomain());
        portMappingDO.setServerPort(req.getServerPort());
        portMappingDO.setClientIp(req.getClientIp());
        portMappingDO.setClientPort(req.getClientPort());
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
        // 更新域名映射
        if (NetworkProtocolEnum.isHttp(portMappingDO.getProtocal()) && StrUtil.isNotBlank(proxyConfig.getServer().getTcp().getDomainName()) && StrUtil.isNotBlank(portMappingDO.getSubdomain())) {
            ProxyUtil.setSubdomainToServerPort(portMappingDO.getSubdomain(), portMappingDO.getServerPort());
        }
        return new PortMappingCreateRes();
    }

    public PortMappingUpdateRes update(PortMappingUpdateReq req) {
        LicenseDO licenseDO = licenseMapper.findById(req.getLicenseId());
        ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            // 临时处理，如果当前用户不是管理员，则操作userId不能为1
            ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
        }
        PortPoolDO portPoolDO = portPoolMapper.findByPort(req.getServerPort());
        ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
        ParamCheckUtil.checkExpression(null == portMappingMapper.findByPort(req.getServerPort(), Sets.newHashSet(req.getId())), ExceptionConstant.PORT_CANNOT_REPEAT_MAPPING, req.getServerPort());
        ParamCheckUtil.checkExpression(!portMappingMapper.checkRepeatBySubdomain(req.getSubdomain(), Sets.newHashSet(req.getId())), ExceptionConstant.PORT_MAPPING_SUBDONAME_CONNOT_REPEAT);

        // 查询原端口映射
        PortMappingDO oldPortMappingDO = portMappingMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(oldPortMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

        PortMappingDO portMappingDO = new PortMappingDO();
        portMappingDO.setId(req.getId());
        portMappingDO.setProtocal(req.getProtocal());
        portMappingDO.setSubdomain(req.getSubdomain());
        portMappingDO.setLicenseId(req.getLicenseId());
        portMappingDO.setServerPort(req.getServerPort());
        portMappingDO.setClientIp(req.getClientIp());
        portMappingDO.setClientPort(req.getClientPort());
        portMappingDO.setProxyResponses(req.getProxyResponses());
        portMappingDO.setProxyTimeoutMs(req.getProxyTimeoutMs());
        portMappingDO.setDescription(req.getDescription());
        portMappingDO.setUpdateTime(new Date());
        portMappingDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
        portMappingMapper.updateById(portMappingDO);
        // 更新VisitorChannel
        visitorChannelService.updateVisitorChannelByPortMapping(oldPortMappingDO, portMappingDO);
        // 删除老的域名映射
        if (NetworkProtocolEnum.isHttp(oldPortMappingDO.getProtocal()) && StrUtil.isNotBlank(oldPortMappingDO.getSubdomain())) {
            ProxyUtil.removeSubdomainToServerPort(oldPortMappingDO.getSubdomain());
        }
        // 更新域名映射
        if (NetworkProtocolEnum.isHttp(portMappingDO.getProtocal()) && StrUtil.isNotBlank(proxyConfig.getServer().getTcp().getDomainName()) && StrUtil.isNotBlank(portMappingDO.getSubdomain())) {
            ProxyUtil.setSubdomainToServerPort(portMappingDO.getSubdomain(), portMappingDO.getServerPort());
        }
        return new PortMappingUpdateRes();
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
        // 更新域名映射
        if (NetworkProtocolEnum.isHttp(portMappingDO.getProtocal()) && StrUtil.isNotBlank(portMappingDO.getSubdomain())) {
            ProxyUtil.removeSubdomainToServerPort(portMappingDO.getSubdomain());
        }
    }

    public void portBindSecurityGroup(Integer portMappingId, Integer groupId) {
        PortMappingDO mappingDO = portMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(groupId);
        mappingDO.setUpdateTime(new Date());
        portMappingMapper.updateById(mappingDO);
        portToSecurityGroupMap.put(mappingDO.getServerPort(), groupId);
    }

    public void portUnbindSecurityGroup(Integer portMappingId) {
        PortMappingDO mappingDO = portMappingMapper.findById(portMappingId);
        if (mappingDO == null) {
            throw new RuntimeException("指定的端口映射不存在");
        }
        mappingDO.setSecurityGroupId(null);
        mappingDO.setUpdateTime(new Date());
        portMappingMapper.updateById(mappingDO);
        portToSecurityGroupMap.remove(mappingDO.getServerPort());
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

    public Integer getSecurityGroupIdByMappingPor(Integer port) {
        return portToSecurityGroupMap.get(port);
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
        portMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());

        // 未配置域名，则不需要处理域名映射逻辑
        if (StrUtil.isBlank(proxyConfig.getServer().getTcp().getDomainName())) {
            return;
        }
        List<PortMappingDO> portMappingDOList = portMappingMapper.selectList(new LambdaQueryWrapper<PortMappingDO>().eq(PortMappingDO::getProtocal, NetworkProtocolEnum.HTTP.getDesc()).isNotNull(PortMappingDO::getSubdomain));
        if (CollectionUtil.isEmpty(portMappingDOList)) {
            return;
        }
        portMappingDOList.forEach(item -> {
            if (StrUtil.isBlank(item.getSubdomain())) {
                return;
            }
            ProxyUtil.setSubdomainToServerPort(item.getSubdomain(), item.getServerPort());
            if (item.getSecurityGroupId() != null) {
                portToSecurityGroupMap.put(item.getServerPort(), item.getSecurityGroupId());
            }
        });
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
