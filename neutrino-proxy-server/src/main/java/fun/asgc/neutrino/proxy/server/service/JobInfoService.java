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
package fun.asgc.neutrino.proxy.server.service;

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.quartz.IJobSource;
import fun.asgc.neutrino.core.quartz.JobInfo;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.web.annotation.RequestBody;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoExecuteReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoListReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoExecuteRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoListRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.dal.JobInfoMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.JobInfoDO;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/5
 */
@Slf4j
@NonIntercept
@Component
public class JobInfoService implements IJobSource {

    @Autowired
    private JobInfoMapper jobInfoMapper;

    public Page<JobInfoListRes> page(PageQuery pageQuery, JobInfoListReq req) {
        Page<JobInfoListRes> page = Page.create(pageQuery);
        jobInfoMapper.page(page, req);
        return page;
    }

    public JobInfoUpdateEnableStatusRes updateEnableStatus(JobInfoUpdateEnableStatusReq req) {
        JobInfoDO jobInfoDO = jobInfoMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(jobInfoDO, ExceptionConstant.JOB_INFO_NOT_EXIST);
        jobInfoMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        return new JobInfoUpdateEnableStatusRes();
    }

    public JobInfoExecuteRes execute(JobInfoExecuteReq req) {


        return new JobInfoExecuteRes();
    }

    @Override
    public List<JobInfo> sourceList() {
        List<JobInfo> jobInfoList = Lists.newArrayList();
        List<JobInfoDO> jobInfoDOList = jobInfoMapper.findEnableList();
        if (CollectionUtil.isEmpty(jobInfoDOList)) {
            return jobInfoList;
        }
        for (JobInfoDO item : jobInfoDOList) {
            jobInfoList.add(new JobInfo()
                    .setId(String.valueOf(item.getId()))
                    .setName(item.getHandler())
                    .setDesc(item.getDesc())
                    .setCron(item.getCron())
                    .setParam(item.getParam())
            );
        }

        return jobInfoList;
    }
}
