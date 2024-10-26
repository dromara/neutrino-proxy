package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.log.UserLoginRecordListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.UserLoginRecordListRes;
import org.dromara.neutrinoproxy.server.service.UserLoginRecordService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author: aoshiguchen
 * @date: 2022/10/20
 */
@Mapping("/user-login-record")
@Controller
public class UserLoginRecordController {
    @Inject
    private UserLoginRecordService userLoginRecordService;

    @Get
    @Mapping("/page")
    public PageInfo<UserLoginRecordListRes> page(PageQuery pageQuery, UserLoginRecordListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return userLoginRecordService.page(pageQuery, req);
    }
}
