package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.Authorization;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoExecuteReq;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoListReq;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoUpdateEnableStatusReq;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoUpdateReq;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoExecuteRes;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoListRes;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoUpdateRes;
import org.dromara.neutrinoproxy.server.dal.entity.JobInfoDO;
import org.dromara.neutrinoproxy.server.service.JobInfoService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.*;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/5
 */
@Slf4j
@Mapping("/job-info")
@Controller
public class JobInfoController {
    @Inject
    private JobInfoService jobInfoService;

    @Get
    @Mapping("/page")
    public PageInfo<JobInfoListRes> page(PageQuery pageQuery, JobInfoListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
        return jobInfoService.page(pageQuery, req);
    }

    @Get
    @Mapping("/findList")
    public List<JobInfoDO> findList() {
        return jobInfoService.findList();
    }

    @Post
    @Mapping("/update/enable-status")
    @Authorization(onlyAdmin = true)
    public JobInfoUpdateEnableStatusRes updateEnableStatus(JobInfoUpdateEnableStatusReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");
        ParamCheckUtil.checkNotNull(req.getEnable(), "enable");
        return jobInfoService.updateEnableStatus(req);
    }

    @Post
    @Mapping("/execute")
    @Authorization(onlyAdmin = true)
    public JobInfoExecuteRes execute(JobInfoExecuteReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");

        return jobInfoService.execute(req);
    }

    @Post
    @Mapping("/update")
    public JobInfoUpdateRes update(JobInfoUpdateReq req) {
        ParamCheckUtil.checkNotNull(req, "req");

        return jobInfoService.update(req);
    }

}
