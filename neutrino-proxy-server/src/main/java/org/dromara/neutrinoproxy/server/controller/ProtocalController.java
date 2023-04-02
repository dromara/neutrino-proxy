package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.res.system.ProtocalListRes;
import org.dromara.neutrinoproxy.server.service.ProtocalService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Mapping("/protocal")
@Controller
public class ProtocalController {

    @Inject
    private ProtocalService protocalService;

    @Get
    @Mapping("/page")
    public PageInfo<ProtocalListRes> page(PageQuery pageQuery) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        List<ProtocalListRes> list = protocalService.list();
        return PageInfo.of(protocalService.list(), (long)list.size(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    @Get
    @Mapping("/list")
    public List<ProtocalListRes> list() {
        return protocalService.list();
    }
}
