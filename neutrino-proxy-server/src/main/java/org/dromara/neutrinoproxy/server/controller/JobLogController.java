package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.log.JobLogListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.JobLogListRes;
import org.dromara.neutrinoproxy.server.service.JobLogService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 *
 * @author: zCans
 * @date: 2022/9/25
 */
@Slf4j
@Mapping("/job-log")
@Controller
public class JobLogController {
    @Inject
    private JobLogService jobLogService;

    @Get
    @Mapping("/page")
    public PageInfo<JobLogListRes> page(PageQuery pageQuery, JobLogListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
        return jobLogService.page(pageQuery, req);
    }

}
