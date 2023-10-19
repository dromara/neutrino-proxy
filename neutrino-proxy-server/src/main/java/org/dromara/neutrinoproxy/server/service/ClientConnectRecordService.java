package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.controller.req.log.ClientConnectRecordListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.ClientConnectRecordListRes;
import org.dromara.neutrinoproxy.server.dal.ClientConnectRecordMapper;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.ClientConnectRecordDO;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Slf4j
@Component
public class ClientConnectRecordService {
    @Inject
    private MapperFacade mapperFacade;
    @Db
    private ClientConnectRecordMapper clientConnectRecordMapper;
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private UserMapper userMapper;

    public void add(ClientConnectRecordDO clientConnectRecordDO) {
        clientConnectRecordMapper.insert(clientConnectRecordDO);
    }

    public PageInfo<ClientConnectRecordListRes> page(PageQuery pageQuery, ClientConnectRecordListReq req) {
        Page<ClientConnectRecordDO> pageResult = clientConnectRecordMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<ClientConnectRecordDO>()
            .eq(null != req.getLicenseId(), ClientConnectRecordDO::getLicenseId, req.getLicenseId())
            .orderByDesc(ClientConnectRecordDO::getId)
        );
        List<ClientConnectRecordListRes> respList = mapperFacade.mapAsList(pageResult.getRecords(), ClientConnectRecordListRes.class);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageInfo.of(respList, pageResult.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        Set<Integer> licenseIds = respList.stream().map(ClientConnectRecordListRes::getLicenseId).collect(Collectors.toSet());
        List<LicenseDO> licenseList = licenseMapper.findByIds(licenseIds);
        if (CollectionUtil.isEmpty(licenseList)) {
            return PageInfo.of(respList, pageResult.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        Set<Integer> userIds = licenseList.stream().map(LicenseDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userList = userMapper.findByIds(userIds);
        Map<Integer, LicenseDO> licenseMap = licenseList.stream().collect(Collectors.toMap(LicenseDO::getId, Function.identity()));
        Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
        boolean isAdmin = SystemContextHolder.isAdmin();
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
            if (!isAdmin) {
                // msg可能带有license等敏感信息，若登录者为游客，则不展示
                item.setMsg("******");
            }
        });
        return PageInfo.of(respList, pageResult);
    }
}
