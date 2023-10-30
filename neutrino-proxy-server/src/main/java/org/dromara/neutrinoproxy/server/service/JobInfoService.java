package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoExecuteReq;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoListReq;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoUpdateEnableStatusReq;
import org.dromara.neutrinoproxy.server.controller.req.system.JobInfoUpdateReq;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoExecuteRes;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoListRes;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.system.JobInfoUpdateRes;
import org.dromara.neutrinoproxy.server.dal.JobInfoMapper;
import org.dromara.neutrinoproxy.server.dal.entity.JobInfoDO;
import org.dromara.neutrinoproxy.server.job.DataCleanJob;
import org.dromara.neutrinoproxy.server.job.DemoJob;
import org.dromara.neutrinoproxy.server.job.FlowReportForDayJob;
import org.dromara.neutrinoproxy.server.job.FlowReportForHourJob;
import org.dromara.neutrinoproxy.server.job.FlowReportForMinuteJob;
import org.dromara.neutrinoproxy.server.job.FlowReportForMonthJob;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.solonplugins.job.IJobHandler;
import org.dromara.solonplugins.job.IJobSource;
import org.dromara.solonplugins.job.JobInfo;
import org.dromara.solonplugins.job.impl.JobExecutor;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.runtime.NativeDetector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: aoshiguchen
 * @date: 2022/9/5
 */
@Slf4j
@Component
public class JobInfoService implements IJobSource {
    @Db
    private JobInfoMapper jobInfoMapper;
    @Inject
    private DataCleanJob dataCleanJob;
    @Inject
    private DemoJob demoJob;
    @Inject
    private FlowReportForDayJob flowReportForDayJob;
    @Inject
    private FlowReportForHourJob flowReportForHourJob;
    @Inject
    private FlowReportForMinuteJob flowReportForMinuteJob;
    @Inject
    private FlowReportForMonthJob flowReportForMonthJob;
    private Map<String, IJobHandler> jobHandlerMap = new HashMap<>();

    @Init
    public void init() {
        // aot 阶段，不初始化
        if (NativeDetector.isAotRuntime()) {
            return;
        }
        jobHandlerMap.put("DataCleanJob", dataCleanJob);
        jobHandlerMap.put("DemoJob", demoJob);
        jobHandlerMap.put("FlowReportForDayJob", flowReportForDayJob);
        jobHandlerMap.put("FlowReportForHourJob", flowReportForHourJob);
        jobHandlerMap.put("FlowReportForMinuteJob", flowReportForMinuteJob);
        jobHandlerMap.put("FlowReportForMonthJob", flowReportForMonthJob);
    }

    public PageInfo<JobInfoListRes> page(PageQuery pageQuery, JobInfoListReq req) {
        Page<JobInfoDO> page = jobInfoMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<JobInfoDO>()
            .orderByAsc(JobInfoDO::getId)
        );
        List<JobInfoListRes> respList = page.getRecords().stream().map(JobInfoDO::toRes).collect(Collectors.toList());
        return PageInfo.of(respList, page);
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
            Solon.context().getBean(JobExecutor.class).startById(String.valueOf(req.getId()));
        } else {
            Solon.context().getBean(JobExecutor.class).stopById(String.valueOf(req.getId()));
        }
        return new JobInfoUpdateEnableStatusRes();
    }

    public JobInfoExecuteRes execute(JobInfoExecuteReq req) {
        Solon.context().getBean(JobExecutor.class).triggerById(String.valueOf(req.getId()), req.getParam());
        return new JobInfoExecuteRes();
    }

    @Override
    public List<JobInfo> sourceList() {
        // aot 阶段，不查数据库
        if (NativeDetector.isAotRuntime()) {
            return Collections.emptyList();
        }
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
                .setJobHandler(jobHandlerMap.get(item.getHandler()))
            );
        }

        return jobInfoList;
    }

    public JobInfoUpdateRes update(JobInfoUpdateReq req) {
        JobInfoDO jobInfoDO = jobInfoMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(jobInfoDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);
        JobInfoDO jobInfo = new JobInfoDO();
        jobInfo.setId(req.getId());
        jobInfo.setCron(req.getCron());
        jobInfo.setDesc(req.getDesc());
        jobInfo.setAlarmEmail(req.getAlarmEmail());
        jobInfo.setAlarmDing(req.getAlarmDing());
        jobInfo.setParam(req.getParam());
        jobInfo.setUpdateTime(new Date());

        jobInfoMapper.updateById(jobInfo);
        return new JobInfoUpdateRes();
    }
}
