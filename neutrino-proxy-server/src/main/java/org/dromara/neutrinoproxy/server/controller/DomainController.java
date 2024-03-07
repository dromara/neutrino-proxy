package org.dromara.neutrinoproxy.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.*;
import org.dromara.neutrinoproxy.server.dal.entity.DomainMappingDO;
import org.dromara.neutrinoproxy.server.service.DomainMappingService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Valid
@Mapping("/domain")
@Controller
public class DomainController {
    @Inject
    private ProxyConfig proxyConfig;

    @Inject
    private DomainMappingService domainMappingService;

    @Get
    @Mapping("/page")
    public PageInfo<DomainMappingDto> page(PageQuery pageQuery, DomainMappingDto req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
        return domainMappingService.page(pageQuery, req);
    }

    @Post
    @Mapping("/modify")
    public PortMappingCreateRes modify(@Validated DomainMappingDO req) {
        if (StringUtils.isBlank(req.getTargetPath())) {// targetPath，默认为127.0.0.1
            req.setTargetPath("127.0.0.1");
        }
        req.setProtocal(NetworkProtocolEnum.HTTP.getDesc());
        if (null == req.getSecurityGroupId()) {
            req.setSecurityGroupId(0);
        }
        return domainMappingService.modify(req);
    }

    @Get
    @Mapping("/one/{id}")
    public DomainMappingDto one(Integer id) {
        return domainMappingService.one(id);
    }

    @Post
    @Mapping("/update/enable-status")
    public PortMappingUpdateEnableStatusRes updateEnableStatus(PortMappingUpdateEnableStatusReq req) {
        return domainMappingService.updateEnableStatus(req);
    }

    @Get
    @Mapping("/delete/{id}")
    public void delete(Integer id) {
        domainMappingService.delete(id);
    }

    /**
     * 绑定安全组
     * @param req portMappingId和securityGroupId
     */
    @Post
    @Mapping("/bind/security-group")
    public void bindSecurityGroup(PortMappingBindSecurityGroupReq req) {
        domainMappingService.domainBindSecurityGroup(req.getId(), req.getSecurityGroupId());
    }

    /**
     * 安全组解绑
     * @param id 端口映射Id
     */
    @Post
    @Mapping("/unbind/security-group")
    public void unbindSecurityGroup(Integer id) {
        domainMappingService.domainUnbindSecurityGroup(id);
    }



    /**
     * 绑定安全组
     * @param domain
     */
    @Get
    @Mapping("/available")
    public boolean exitDomain(@NotBlank String domain, Integer id) {
       return domainMappingService.exitDomain(domain, id);
    }

}
