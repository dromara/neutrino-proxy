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
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.quartz.IJobSource;
import fun.asgc.neutrino.core.quartz.JobExecutor;
import fun.asgc.neutrino.core.quartz.JobInfo;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoExecuteReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoListReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoExecuteRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoListRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoUpdateRes;
import fun.asgc.neutrino.proxy.server.dal.JobInfoMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.JobInfoDO;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/5
 */
@Slf4j
@Component
public class JobInfoService implements IJobSource {
    @Inject
    private JobInfoMapper jobInfoMapper;

    public Page<JobInfoListRes> page(PageQuery pageQuery, JobInfoListReq req) {
        Page<JobInfoListRes> page = Page.create(pageQuery);
        jobInfoMapper.page(page, req);
        return page;
    }

    public List<JobInfoDO> findList() {
        List<JobInfoDO> jobInfoDOList = jobInfoMapper.findList();
        return jobInfoDOList;
    }

    public JobInfoUpdateEnableStatusRes updateEnableStatus(JobInfoUpdateEnableStatusReq req) {
        JobInfoDO jobInfoDO = jobInfoMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(jobInfoDO, ExceptionConstant.JOB_INFO_NOT_EXIST);
        jobInfoMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        if (EnableStatusEnum.ENABLE.getStatus().equals(req.getEnable())) {
            Solon.context().getBean(JobExecutor.class).add(new JobInfo()
                    .setId(String.valueOf(jobInfoDO.getId()))
                    .setName(jobInfoDO.getHandler())
                    .setDesc(jobInfoDO.getDesc())
                    .setCron(jobInfoDO.getCron())
                    .setParam(jobInfoDO.getParam())
                    .setEnable(true)
            );
        } else {
            Solon.context().getBean(JobExecutor.class).remove(String.valueOf(req.getId()));
        }
        return new JobInfoUpdateEnableStatusRes();
    }

    public JobInfoExecuteRes execute(JobInfoExecuteReq req) {
        Solon.context().getBean(JobExecutor.class).trigger(String.valueOf(req.getId()), req.getParam());
        return new JobInfoExecuteRes();
    }

    @Override
    public List<JobInfo> sourceList() {
        List<JobInfo> jobInfoList = Lists.newArrayList();
        List<JobInfoDO> jobInfoDOList = jobInfoMapper.findList();
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
                    .setEnable(EnableStatusEnum.ENABLE.getStatus().equals(item.getEnable()))
            );
        }

        return jobInfoList;
    }

    public JobInfoUpdateRes update(JobInfoUpdateReq req) {
        JobInfoDO  jobInfoDO = jobInfoMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull( jobInfoDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);
        JobInfoDO jobInfo = new JobInfoDO();
        jobInfo.setId(req.getId());
        jobInfo.setCron(req.getCron());
        jobInfo.setDesc(req.getDesc());
        jobInfo.setAlarmEmail(req.getAlarmEmail());
        jobInfo.setAlarmDing(req.getAlarmDing());
        jobInfo.setParam(req.getParam());
        jobInfo.setUpdateTime(new Date());

        jobInfoMapper.update( jobInfo);
        return new JobInfoUpdateRes();
    }
}
