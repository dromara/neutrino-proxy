package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.log.ClientConnectRecordListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.ClientConnectRecordListRes;
import org.dromara.neutrinoproxy.server.service.ClientConnectRecordService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author: aoshiguchen
 * @date: 2022/11/26
 */
@Slf4j
@Mapping("/client-connect-record")
@Controller
public class ClientConnectRecordController {
    @Inject
    private ClientConnectRecordService clientConnectRecordService;

    @Get
    @Mapping("/page")
    public PageInfo<ClientConnectRecordListRes> page(PageQuery pageQuery, ClientConnectRecordListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
        return clientConnectRecordService.page(pageQuery, req);
    }
}
