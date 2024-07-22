package org.dromara.neutrinoproxy.server.controller;

import cn.hutool.core.util.StrUtil;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.Authorization;
import org.dromara.neutrinoproxy.server.controller.req.proxy.DomainCreateReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.DomainListReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.DomainUpdateReq;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingCreateReq;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainListRes;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;
import org.dromara.neutrinoproxy.server.service.DomainService;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.lang.Nullable;

import javax.sound.midi.Soundbank;
import java.io.IOException;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Mapping("/domain")
@Controller
public class DomainController {
    @Inject
    private DomainService domainService;

    @Mapping("/page")
    public PageInfo<DomainListRes> page(PageQuery pageQuery, DomainListReq req) {
        return domainService.page(pageQuery, req);
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
}
