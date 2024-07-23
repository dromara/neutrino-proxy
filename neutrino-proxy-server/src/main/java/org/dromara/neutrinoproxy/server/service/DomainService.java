package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.DefaultDomainStatusEnum;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.constant.HttpsStatusEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainListRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainUpdateDefaultStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.dal.DomainMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.DomainNameDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.h2.schema.Domain;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.UploadedFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Slf4j
@Component
public class DomainService {
    @Db
    private DomainMapper domainMapper;
    @Db
    private UserMapper userMapper;

    public PageInfo<DomainListRes> page(PageQuery pageQuery, DomainListReq req) {
//        Page<DomainNameDO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
//        LambdaQueryWrapper<DomainNameDO> queryWrapper = Wrappers.<DomainNameDO>lambdaQuery()
//            .eq(req.getEnable() != null, DomainNameDO::getEnable, req.getEnable())
//            .eq(req.getUserId() != null, DomainNameDO::getUserId, req.getUserId());
//        Page<DomainNameDO> page = domainMapper.selectPage(page, queryWrapper);
        Page<DomainNameDO> page = domainMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<DomainNameDO>()
            .eq(req.getEnable() != null, DomainNameDO::getEnable, req.getEnable())
            .eq(req.getUserId() != null, DomainNameDO::getUserId, req.getUserId())
            .orderByAsc(Arrays.asList(DomainNameDO::getUserId, DomainNameDO::getId))
        );
        List<DomainListRes> resList = page.getRecords().stream().map(DomainNameDO::toRes).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageInfo.of(resList, page);
        }
        Set<Integer> userIds = resList.stream().map(DomainListRes::getUserId).collect(Collectors.toSet());
        List<UserDO> userDOList = userMapper.findByIds(userIds);
        Map<Integer, UserDO> userMap = userDOList.stream().collect(Collectors.toMap(UserDO::getId, c -> c));
        for (DomainListRes item : resList) {
            UserDO userDO = userMap.get(item.getUserId());
            if (null != userDO) {
                item.setUserName(userDO.getName());
            }
        }
        return PageInfo.of(resList, page);
    }

    /**
     * 创建域名
     * @param req
     */
    public void create(DomainCreateReq req, UploadedFile jks) throws IOException {
        DomainNameDO domainNameDO = new DomainNameDO();
        if (null != jks) {
            ParamCheckUtil.checkNotEmpty(req.getKeyStorePassword(), "keyStorePassword");

            InputStream content = jks.getContent();
            byte[] byteArray = toByteArray(content);
            domainNameDO.setJks(byteArray);
            domainNameDO.setKeyStorePassword(req.getKeyStorePassword());
        }
        Integer userId = SystemContextHolder.getUserId();
        Date now = new Date();
        domainNameDO.setDomain(req.getDomain());
        domainNameDO.setIsDefault(DefaultDomainStatusEnum.DISABLE.getStatus());
        domainNameDO.setForceHttps(req.getForceHttps() != null ? HttpsStatusEnum.of(req.getForceHttps()).getStatus(): HttpsStatusEnum.DISABLE_ONLY_HTTPS.getStatus());
        domainNameDO.setUserId(userId);
        domainNameDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
        domainNameDO.setCreateTime(now);
        domainNameDO.setUpdateTime(now);
        domainMapper.insert(domainNameDO);
    }

    /**
     * 将 InputStream 转换为 byte[]
      */
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    //TODO 更新相关channel
    public void update(DomainUpdateReq req, UploadedFile jks) throws IOException {
        DomainNameDO domainNameCheck = domainMapper.checkRepeat(req.getDomain(), Sets.newHashSet(req.getId()));
        ParamCheckUtil.checkMustNull(domainNameCheck, ExceptionConstant.DOMAIN_NAME_CANNOT_REPEAT);
        LambdaUpdateWrapper<DomainNameDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DomainNameDO::getId, req.getId());
        if (null != jks) {
            ParamCheckUtil.checkNotEmpty(req.getKeyStorePassword(), "keyStorePassword");

            InputStream content = jks.getContent();
            byte[] byteArray = toByteArray(content);
            updateWrapper.set(DomainNameDO::getKeyStorePassword, req.getKeyStorePassword());
            updateWrapper.set(DomainNameDO::getJks, byteArray);
        }
        updateWrapper.set(DomainNameDO::getDomain, req.getDomain());
        updateWrapper.set(DomainNameDO::getUpdateTime, new Date());
        updateWrapper.set(req.getForceHttps() != null, DomainNameDO::getForceHttps, req.getForceHttps());
        int update = domainMapper.update(updateWrapper);
        System.out.println(update);
    }

    // TODO 更新对应的channel
    public DomainUpdateEnableStatusRes updateEnableStatus(DomainUpdateEnableStatusReq req) {
        if (Objects.equals(req.getEnable(), EnableStatusEnum.DISABLE.getStatus())) {
            //如果设置状态为禁用，则将默认域名设置为非默认域名
            updateDefaultStatus(req.getId(), DefaultDomainStatusEnum.DISABLE.getStatus());
        }
        domainMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        return new DomainUpdateEnableStatusRes();
    }

    //TODO 处理VisitorChannel
    public void delete(Integer id) {
        domainMapper.deleteById(id);
    }

    public DomainUpdateDefaultStatusRes updateDefaultStatus(Integer id, Integer isDefault) {
        if (Objects.equals(isDefault, DefaultDomainStatusEnum.ENABLE.getStatus())) {
            //如果设置状态为默认域名，则将之前的默认域名设置为非默认域名
            DomainNameDO oldDomainNameDO = domainMapper.selectOne(Wrappers.<DomainNameDO>lambdaQuery()
                .select(DomainNameDO::getId)
                .eq(DomainNameDO::getIsDefault, DefaultDomainStatusEnum.ENABLE.getStatus()));
            if (oldDomainNameDO != null) {
                oldDomainNameDO.setIsDefault(DefaultDomainStatusEnum.DISABLE.getStatus());
                domainMapper.updateById(oldDomainNameDO);
            }
        }

        DomainNameDO domainNameDO = new DomainNameDO();
        domainNameDO.setId(id);
        domainNameDO.setIsDefault(isDefault);
        domainMapper.updateById(domainNameDO);

        return new DomainUpdateDefaultStatusRes();
    }
}
