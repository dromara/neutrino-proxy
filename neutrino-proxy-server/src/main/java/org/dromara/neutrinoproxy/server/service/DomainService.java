package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.*;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainListRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainUpdateDefaultStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.dal.DomainMapper;
import org.dromara.neutrinoproxy.server.dal.DomainPortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.DomainNameDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.proxy.enhance.SslContextManager;
import org.dromara.neutrinoproxy.server.service.bo.FullDomainNameBO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
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
    @Db
    private PortMappingMapper portMappingMapper;
    @Db
    private DomainPortMappingMapper domainPortMappingMapper;

    @Inject
    private SslContextManager sslContextManager;


    public PageInfo<DomainListRes> page(PageQuery pageQuery, DomainListReq req) {
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

    public List<DomainListRes> all(DomainListReq req) {
        LambdaQueryWrapper<DomainNameDO> wrapper = Wrappers.<DomainNameDO>lambdaQuery()
            .eq(req.getEnable() != null, DomainNameDO::getEnable, req.getEnable());
        List<DomainNameDO> domainNameDOS = domainMapper.selectList(wrapper);
        if (domainNameDOS == null) {
            return null;
        }
        List<DomainListRes> resList = domainNameDOS.stream().map(DomainNameDO::toRes).toList();
        Set<Integer> userIds = resList.stream().map(DomainListRes::getUserId).collect(Collectors.toSet());
        List<UserDO> userDOList = userMapper.findByIds(userIds);
        Map<Integer, UserDO> userMap = userDOList.stream().collect(Collectors.toMap(UserDO::getId, c -> c));
        for (DomainListRes item : resList) {
            UserDO userDO = userMap.get(item.getUserId());
            if (null != userDO) {
                item.setUserName(userDO.getName());
            }
        }
        return resList;
    }

    /**
     * 创建域名
     *
     * @param req
     */
    public void create(DomainCreateReq req, UploadedFile jks) {
        DomainNameDO domainNameCheck = domainMapper.checkRepeat(req.getDomain(), null);
        ParamCheckUtil.checkMustNull(domainNameCheck, ExceptionConstant.DOMAIN_NAME_CANNOT_REPEAT);
        DomainNameDO domainNameDO = new DomainNameDO();
        if (null != jks) {
            try {
                ParamCheckUtil.checkNotEmpty(req.getKeyStorePassword(), "keyStorePassword");

                InputStream content = jks.getContent();
                byte[] byteArray = toByteArray(content);
                domainNameDO.setJks(byteArray);
                domainNameDO.setKeyStorePassword(req.getKeyStorePassword());
                //添加证书
                sslContextManager.addDomainAndCert(req.getDomain(), byteArray, req.getKeyStorePassword());
            } catch (Exception e) {
                log.error("证书添加失败", e);
                e.printStackTrace();
            }
        }
        Integer userId = SystemContextHolder.getUserId();
        Date now = new Date();
        domainNameDO.setDomain(req.getDomain());
        domainNameDO.setIsDefault(DefaultDomainStatusEnum.DISABLE.getStatus());
        domainNameDO.setForceHttps(req.getForceHttps() != null ? HttpsStatusEnum.of(req.getForceHttps()).getStatus() : HttpsStatusEnum.DISABLE_ONLY_HTTPS.getStatus());
        domainNameDO.setUserId(userId);
        domainNameDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
        domainNameDO.setCreateTime(now);
        domainNameDO.setUpdateTime(now);
        domainMapper.insert(domainNameDO);
        //更新主域名到域名id映射
        ProxyUtil.setDomainToDomainNameId(domainNameDO.getDomain(), domainNameDO.getId());
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

    public void update(DomainUpdateReq req, UploadedFile jks) {
        DomainNameDO domainNameCheck = domainMapper.checkRepeat(req.getDomain(), Sets.newHashSet(req.getId()));
        ParamCheckUtil.checkMustNull(domainNameCheck, ExceptionConstant.DOMAIN_NAME_CANNOT_REPEAT);

        DomainNameDO oldDomainNameDO = domainMapper.selectOne(Wrappers.<DomainNameDO>lambdaQuery().eq(DomainNameDO::getId, req.getId()));
        List<FullDomainNameBO> oldFullDomainNameBOS = domainMapper.selectFullDomainNameListByDomainNameIds(Sets.newHashSet(req.getId()));
        LambdaUpdateWrapper<DomainNameDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DomainNameDO::getId, req.getId());
        if (null != jks) {
            try {
                ParamCheckUtil.checkNotEmpty(req.getKeyStorePassword(), "keyStorePassword");

                InputStream content = jks.getContent();
                byte[] byteArray = toByteArray(content);
                updateWrapper.set(DomainNameDO::getKeyStorePassword, req.getKeyStorePassword());
                updateWrapper.set(DomainNameDO::getJks, byteArray);
                //添加证书
                sslContextManager.addDomainAndCert(req.getDomain(), byteArray, req.getKeyStorePassword());
            } catch (Exception e) {
                log.error("证书添加失败", e);
                e.printStackTrace();
            }
        }
        updateWrapper.set(DomainNameDO::getDomain, req.getDomain());
        updateWrapper.set(DomainNameDO::getUpdateTime, new Date());
        updateWrapper.set(req.getForceHttps() != null, DomainNameDO::getForceHttps, req.getForceHttps());
        domainMapper.update(updateWrapper);

        //更新域名相关映射
        if (!Objects.equals(oldDomainNameDO.getDomain(), req.getDomain())) {
            //更新主域名到域名id映射
            ProxyUtil.removeDomainToDomainNameId(oldDomainNameDO.getDomain());
            ProxyUtil.setDomainToDomainNameId(req.getDomain(), req.getId());
            if (CollectionUtil.isEmpty(oldFullDomainNameBOS)) return;
            //存在域名映射，更新完整域名到服务器端口映射
            for (FullDomainNameBO oldFullDomainNameBO : oldFullDomainNameBOS) {
                String oldFullDomain = StrUtil.join(StrPool.DOT, oldFullDomainNameBO.getSubdomain(), oldFullDomainNameBO.getDomain());
                Integer serverPort = ProxyUtil.getServerPortByFullDomain(oldFullDomain);
                ProxyUtil.removeFullDomainToServerPort(oldFullDomain);
                ProxyUtil.setFullDomainToServerPort(StrUtil.join(StrPool.DOT, oldFullDomainNameBO.getSubdomain(), req.getDomain()), serverPort);
            }
        }
    }

    public DomainUpdateEnableStatusRes updateEnableStatus(DomainUpdateEnableStatusReq req) {
        if (Objects.equals(req.getEnable(), EnableStatusEnum.DISABLE.getStatus())) {
            //如果设置状态为禁用，则将默认域名设置为非默认域名
            updateDefaultStatus(req.getId(), DefaultDomainStatusEnum.DISABLE.getStatus());
        }
        domainMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        if (Objects.equals(req.getEnable(), EnableStatusEnum.ENABLE.getStatus())) {
            ProxyUtil.setDomainToDomainNameId(req.getDomain(), req.getId());
        } else {
            ProxyUtil.removeDomainToDomainNameId(req.getDomain());
        }
        return new DomainUpdateEnableStatusRes();
    }

    public void delete(Integer domainNameId) {
        //检查当前域名是否正在使用
        boolean checkUsed = domainPortMappingMapper.checkUsed(domainNameId);
        ParamCheckUtil.checkExpression(!checkUsed, ExceptionConstant.DOMAIN_NAME_IS_USED);
        DomainNameDO domainNameDO = domainMapper.selectOne(Wrappers.<DomainNameDO>lambdaQuery()
            .eq(DomainNameDO::getId, domainNameId)
            .select(DomainNameDO::getDomain, DomainNameDO::getEnable));
        if (Objects.equals(domainNameDO.getEnable(), EnableStatusEnum.ENABLE.getStatus())) {
            ProxyUtil.removeDomainToDomainNameId(domainNameDO.getDomain());
        }
        domainMapper.deleteById(domainNameId);
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

    @Init
    public void init() {
        List<FullDomainNameBO> fullDomainNameBOS = domainMapper.selectFullDomainNameList();
        if (CollectionUtil.isEmpty(fullDomainNameBOS)) {
            return;
        }
        Set<Integer> portMappingIds = fullDomainNameBOS.stream().map(FullDomainNameBO::getPortMappingId).collect(Collectors.toSet());
        List<PortMappingDO> portMappingDOS = portMappingMapper.selectBatchIds(portMappingIds);
        if (CollectionUtil.isEmpty(portMappingDOS)) {
            return;
        }
        Map<Integer, PortMappingDO> portMappingDOMap = portMappingDOS.stream()
            .filter(item -> NetworkProtocolEnum.HTTP.getDesc().equals(item.getProtocal()))
            .collect(Collectors.toMap(PortMappingDO::getId, Function.identity()));
        fullDomainNameBOS.forEach(item -> {
            PortMappingDO portMappingDO = portMappingDOMap.get(item.getPortMappingId());
            if (null == portMappingDO) {
                return;
            }
            String fullDomain = StrUtil.join(StrPool.DOT, item.getSubdomain(), item.getDomain());
            //更新完整域名到端口映射
            ProxyUtil.setFullDomainToServerPort(fullDomain, portMappingDO.getServerPort());
        });

        //更新主域名到主域名id缓存，未禁用
        List<DomainNameDO> domainNameDOS = domainMapper.selectList(Wrappers.<DomainNameDO>lambdaQuery()
            .eq(DomainNameDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
            .select(DomainNameDO::getDomain, DomainNameDO::getId));
        for (DomainNameDO domainNameDO : domainNameDOS) {
            ProxyUtil.setDomainToDomainNameId(domainNameDO.getDomain(), domainNameDO.getId());
        }
    }

    public boolean isOnlyHttps(Integer domainNameId) {
        ParamCheckUtil.checkExpression((domainNameId != null && domainNameId > 0), ExceptionConstant.DOMAIN_NAME_NOT_EXIST);
        DomainNameDO domainNameDO = domainMapper.selectOne(Wrappers.<DomainNameDO>lambdaQuery()
            .eq(DomainNameDO::getId, domainNameId));
        return domainNameDO != null && Objects.equals(domainNameDO.getForceHttps(), HttpsStatusEnum.ONLY_HTTPS.getStatus());
    }
}
