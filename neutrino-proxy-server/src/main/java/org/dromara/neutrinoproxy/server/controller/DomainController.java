package org.dromara.neutrinoproxy.server.controller;

import cn.hutool.core.util.StrUtil;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.Authorization;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainListRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainUpdateDefaultStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;
import org.dromara.neutrinoproxy.server.service.DomainService;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.lang.Nullable;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.List;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Mapping("/domain")
@Controller
public class DomainController {
    @Inject
    private DomainService domainService;

    @Get
    @Mapping("/page")
    public PageInfo<DomainListRes> page(PageQuery pageQuery, DomainListReq req) {
        return domainService.page(pageQuery, req);
    }
    @Get
    @Mapping("/all")
    public List<DomainListRes> all(DomainListReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        return domainService.all(req);
    }

    @Post
    @Mapping(path = "/create")
    @Authorization(onlyAdmin = true)
    public void create(DomainCreateReq req, UploadedFile jks) throws IOException {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotEmpty(req.getDomain(), "domain");
        domainService.create(req, jks);
    }

    @Post
    @Mapping(path = "/update")
    @Authorization(onlyAdmin = true)
    public void update(DomainUpdateReq req, UploadedFile jks) throws IOException {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotEmpty(req.getDomain(), "domain");
        domainService.update(req, jks);
    }

    @Post
    @Mapping(path = "/update/enable-status")
    @Authorization(onlyAdmin = true)
    public DomainUpdateEnableStatusRes updateEnableStatus(DomainUpdateEnableStatusReq req){
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");
        ParamCheckUtil.checkNotNull(req.getEnable(), "enable");
        return domainService.updateEnableStatus(req);
    }

    @Post
    @Mapping(path = "/delete")
    @Authorization(onlyAdmin = true)
    public void delete(DomainDeleteReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");

        domainService.delete(req.getId());
    }

    @Post
    @Mapping(path = "/update/default-status")
    @Authorization(onlyAdmin = true)
    public DomainUpdateDefaultStatusRes updateDefaultStatus(DomainUpdateDefaultStatusReq req){
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");
        ParamCheckUtil.checkNotNull(req.getIsDefault(), "isDefault");
        return domainService.updateDefaultStatus(req.getId(), req.getIsDefault());
    }
}
