/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.Authorization;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoExecuteReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoListReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoExecuteRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoListRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoUpdateRes;
import fun.asgc.neutrino.proxy.server.dal.entity.JobInfoDO;
import fun.asgc.neutrino.proxy.server.service.JobInfoService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
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
