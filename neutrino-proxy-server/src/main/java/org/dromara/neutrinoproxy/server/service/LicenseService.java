package org.dromara.neutrinoproxy.server.service;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import com.google.common.collect.Sets;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.db.DBInitialize;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.ServiceException;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.LicenseCreateReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.LicenseListReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.LicenseUpdateEnableStatusReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.LicenseUpdateReq;
import org.dromara.neutrinoproxy.server.controller.res.proxy.LicenseCreateRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.LicenseDetailRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.LicenseListRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.LicenseUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.LicenseUpdateRes;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.service.bo.FlowLimitBO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.util.StringUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.data.annotation.Tran;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * license服务
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Component
public class LicenseService implements LifecycleBean {
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private PortMappingMapper portMappingMapper;
    @Db
    private UserMapper userMapper;
    @Inject
    private VisitorChannelService visitorChannelService;
    @Inject
    private DBInitialize dbInitialize;
    // 流量限制缓存
    private final Cache<Integer, FlowLimitBO> flowLimitCache = CacheUtil.newLRUCache(200, 1000 * 60 * 5);

    public PageInfo<LicenseListRes> page(PageQuery pageQuery, LicenseListReq req) {
        Page<LicenseDO> page = licenseMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<LicenseDO>()
            .eq(req.getUserId() != null, LicenseDO::getUserId, req.getUserId())
            .eq(req.getIsOnline() != null, LicenseDO::getIsOnline, req.getIsOnline())
            .eq(req.getEnable() != null, LicenseDO::getEnable, req.getEnable())
            .orderByAsc(Arrays.asList(LicenseDO::getUserId, LicenseDO::getId))
        );
        List<LicenseListRes> respList = page.getRecords().stream().map(LicenseDO::toRes).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageInfo.of(respList, page);
        }
        if (!CollectionUtil.isEmpty(respList)) {
            Set<Integer> userIds = respList.stream().map(LicenseListRes::getUserId).collect(Collectors.toSet());
            List<UserDO> userList = userMapper.findByIds(userIds);
            Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
            for (LicenseListRes item : respList) {
                UserDO userDO = userMap.get(item.getUserId());
                if (null != userDO) {
                    item.setUserName(userDO.getName());
                }
                item.setKey(desensitization(item.getUserId(), item.getKey()));
            }
        }
        return PageInfo.of(respList, page);
    }

    public List<LicenseListRes> list(LicenseListReq req) {
        List<LicenseDO> list = licenseMapper.selectList(new LambdaQueryWrapper<LicenseDO>()
                .eq(null != req.getEnable(), LicenseDO::getEnable, req.getEnable())
        );
        List<LicenseListRes> licenseList = assembleConvertLicenses(list);
        return licenseList;
    }

    private List<LicenseListRes> assembleConvertLicenses(List<LicenseDO> list) {
        List<LicenseListRes> licenseList = list.stream().map(LicenseDO::toRes).collect(Collectors.toList());
        // 插入用户名
        Set<Integer> userIds = licenseList.stream().map(LicenseListRes::getUserId).collect(Collectors.toSet());
        List<UserDO> userList = userMapper.findByIds(userIds);
        Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
        for (LicenseListRes item : licenseList) {
            UserDO userDO = userMap.get(item.getUserId());
            if (null != userDO) {
                item.setUserName(userDO.getName());
            }
            item.setKey(desensitization(item.getUserId(), item.getKey()));
        }
        return licenseList;
    }

    /**
     * 创建license
     *
     * @param req
     * @return
     */
    public LicenseCreateRes create(LicenseCreateReq req) {
        LicenseDO licenseDO = licenseMapper.checkRepeat(req.getUserId(), req.getName());
        ParamCheckUtil.checkExpression(null == licenseDO, ExceptionConstant.LICENSE_NAME_CANNOT_REPEAT);

        String key = UUID.randomUUID().toString().replaceAll("-", "");
        Date now = new Date();

        licenseDO = new LicenseDO()
            .setName(req.getName())
            .setKey(key)
            .setUserId(req.getUserId())
            .setUpLimitRate(req.getUpLimitRate())
            .setDownLimitRate(req.getDownLimitRate())
            .setIsOnline(OnlineStatusEnum.OFFLINE.getStatus())
            .setEnable(EnableStatusEnum.ENABLE.getStatus())
            .setCreateTime(now)
            .setUpdateTime(now);

        licenseMapper.insert(licenseDO);

        // 刷新流量限制缓存
        refreshFlowLimitCache(licenseDO.getId(), licenseDO.getUpLimitRate(), licenseDO.getDownLimitRate());
        return new LicenseCreateRes();
    }

    public LicenseUpdateRes update(LicenseUpdateReq req) {
        LicenseDO oldLicenseDO = licenseMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(oldLicenseDO, ExceptionConstant.LICENSE_NOT_EXIST);

        LicenseDO licenseCheck = licenseMapper.checkRepeat(oldLicenseDO.getUserId(), req.getName(), Sets.newHashSet(oldLicenseDO.getId()));
        ParamCheckUtil.checkMustNull(licenseCheck, ExceptionConstant.LICENSE_NAME_CANNOT_REPEAT);

        licenseMapper.update(null, new LambdaUpdateWrapper<LicenseDO>()
            .eq(LicenseDO::getId, req.getId())
            .set(LicenseDO::getName, req.getName())
            .set(LicenseDO::getUpLimitRate, req.getUpLimitRate())
            .set(LicenseDO::getDownLimitRate, req.getDownLimitRate())
            .set(LicenseDO::getUpdateTime, new Date())
        );

        // 刷新流量限制缓存
        refreshFlowLimitCache(req.getId(), req.getUpLimitRate(), req.getDownLimitRate());

        return new LicenseUpdateRes();
    }

    public LicenseDetailRes detail(Integer id) {
        LicenseDO licenseDO = licenseMapper.findById(id);
        if (null == licenseDO) {
            return null;
        }
        UserDO userDO = userMapper.findById(licenseDO.getUserId());
        String userName = "";
        if (null != userDO) {
            userName = userDO.getName();
        }
        return new LicenseDetailRes()
                .setId(licenseDO.getId())
                .setName(licenseDO.getName())
                .setKey(desensitization(licenseDO.getUserId(), licenseDO.getKey()))
                .setUserId(licenseDO.getUserId())
                .setUserName(userName)
                .setIsOnline(licenseDO.getIsOnline())
                .setEnable(licenseDO.getEnable())
                .setCreateTime(licenseDO.getCreateTime())
                .setUpdateTime(licenseDO.getUpdateTime())
                ;
    }

    /**
     * 更新license启用状态
     *
     * @param req
     * @return
     */
    @Tran
    public LicenseUpdateEnableStatusRes updateEnableStatus(LicenseUpdateEnableStatusReq req) {
        // 服务端免通道 直接转发，所以无需建立客户端通道。启用即在线，禁用即离线 20240311 设计取消
//        if (req.getId() == 1) {
//            licenseMapper.update(null, new LambdaUpdateWrapper<LicenseDO>()
//                .eq(LicenseDO::getId, req.getId())
//                .set(LicenseDO::getEnable, req.getEnable())
//                .set(LicenseDO::getIsOnline, req.getEnable())
//                .set(LicenseDO::getUpdateTime, new Date()));
//        } else {
            licenseMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
            // 更新VisitorChannel
            visitorChannelService.updateVisitorChannelByLicenseId(req.getId(), req.getEnable());
//        }
        return new LicenseUpdateEnableStatusRes();
    }

    /**
     * 删除license
     *
     * @param id
     */
    public void delete(Integer id) {
        List<PortMappingDO> portMappingDOList = portMappingMapper.findListByLicenseId(id);
        if (CollectionUtil.isNotEmpty(portMappingDOList)) {
            throw ServiceException.create(ExceptionConstant.LICENSE_CANNOT_BE_DELETED, portMappingDOList.size());
        }
        licenseMapper.deleteById(id);
        // 更新VisitorChannel
        visitorChannelService.updateVisitorChannelByLicenseId(id, EnableStatusEnum.DISABLE.getStatus());
        // 删除流量限制缓存
        flowLimitCache.remove(id);
    }

    /**
     * 重置license
     *
     * @param id
     */
    public void reset(Integer id) {
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        Date now = new Date();

        licenseMapper.reset(id, key, now);
    }

    public LicenseDO findByKey(String license) {
        return licenseMapper.findByKey(license);
    }

    /**
     * 脱敏处理
     * 非当前登录人的license，一律脱敏
     *
     * @param userId
     * @param licenseKey
     * @return
     */
    private String desensitization(Integer userId, String licenseKey) {
        Integer currentUserId = SystemContextHolder.getUser().getId();
        if (currentUserId.equals(userId)) {
            return licenseKey;
        }
        return licenseKey.substring(0, 10) + "****" + licenseKey.substring(licenseKey.length() - 10);
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
        licenseMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
        // 刷新流量限制缓存
        List<LicenseDO> licenseDOList = licenseMapper.listAll();
        if (CollectionUtils.isEmpty(licenseDOList)) {
            for (LicenseDO licenseDO : licenseDOList) {
                refreshFlowLimitCache(licenseDO.getId(), licenseDO.getUpLimitRate(), licenseDO.getDownLimitRate());
            }
        }
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
     * @param licenseId
     * @return
     */
    public FlowLimitBO getFlowLimit(Integer licenseId) {
        FlowLimitBO res = flowLimitCache.get(licenseId);
        if (null == res) {
            LicenseDO licenseDO = licenseMapper.queryById(licenseId);
            if (null != licenseDO) {
                refreshFlowLimitCache(licenseId, licenseDO.getUpLimitRate(), licenseDO.getDownLimitRate());
                res = flowLimitCache.get(licenseId);
            }
        }
        return res;
    }

    @Override
    public void start() throws Throwable {

    }

    /**
     * 服务端项目停止、启动时，更新在线状态为离线
     */
    @Override
    public void stop() throws Throwable {
        licenseMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
    }

    /**
     * 查询当前角色下的license，若为管理员 则返回全部license
     */
    public List<LicenseListRes> queryCurUserLicense(LicenseListReq req) {
        if (SystemContextHolder.isAdmin()) {
            return this.list(req);
        }

        List<LicenseDO> list = licenseMapper.selectList(new LambdaQueryWrapper<LicenseDO>()
                .eq(LicenseDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
                .eq(LicenseDO::getUserId, SystemContextHolder.getUserId())
        );
        if (CollectionUtil.isEmpty(list)) return null;
        List<LicenseListRes> licenseList = assembleConvertLicenses(list);
        return licenseList;
    }
}
