package fun.asgc.neutrino.proxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
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
import fun.asgc.solon.extend.job.IJobSource;
import fun.asgc.solon.extend.job.JobInfo;
import fun.asgc.solon.extend.job.impl.JobExecutor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.ibatis.solon.annotation.Db;
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
    private MapperFactory mapperFactory;
    @Db
    private JobInfoMapper jobInfoMapper;

    public PageInfo<JobInfoListRes> page(PageQuery pageQuery, JobInfoListReq req) {
        Page<JobInfoListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        List<JobInfoDO> list = jobInfoMapper.selectList(new LambdaQueryWrapper<JobInfoDO>()
                .orderByAsc(JobInfoDO::getId)
        );
        List<JobInfoListRes> respList = mapperFactory.getMapperFacade().mapAsList(list, JobInfoListRes.class);
        return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
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

        jobInfoMapper.updateById(jobInfo);
        return new JobInfoUpdateRes();
    }
}
