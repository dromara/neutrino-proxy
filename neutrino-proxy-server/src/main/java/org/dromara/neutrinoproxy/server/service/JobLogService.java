package org.dromara.neutrinoproxy.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.log.JobLogListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.JobLogListRes;
import org.dromara.neutrinoproxy.server.dal.JobLogMapper;
import org.dromara.neutrinoproxy.server.dal.entity.JobLogDO;
import org.dromara.solonplugins.job.IJobCallback;
import org.dromara.solonplugins.job.JobInfo;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.runtime.NativeDetector;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@Component
public class JobLogService implements IJobCallback {
	@Db
	private JobLogMapper jobLogMapper;

	@Override
	public void executeLog(JobInfo jobInfo, String param, Throwable throwable) {
        // aot 阶段，不查数据库
        if (NativeDetector.isAotRuntime()) {
            return;
        }
		Integer code = 0;
		String msg = "";
		if (null == throwable) {
			msg = "execute success";
			log.debug("job[id={},name={}]execute success", jobInfo.getId(), jobInfo.getName());
		} else {
			log.error("job[id={},name={}]execute error", jobInfo.getId(), jobInfo.getName(), throwable);
			msg = "execute error:\r\n" + ExceptionUtils.getStackTrace(throwable);
			code = -1;
		}
		jobLogMapper.insert(new JobLogDO()
				.setJobId(Integer.valueOf(jobInfo.getId()))
				.setHandler(jobInfo.getName())
				.setParam(param)
				.setCode(code)
				.setMsg(msg)
				.setAlarmStatus(0)
				.setCreateTime(new Date())
		);
	}

	public PageInfo<JobLogListRes> page(PageQuery pageQuery, JobLogListReq req) {
        Page<JobLogDO> page = jobLogMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<JobLogDO>()
            .eq(null != req.getJobId(), JobLogDO::getJobId, req.getJobId())
            .orderByDesc(JobLogDO::getId)
        );
        List<JobLogListRes> respList = page.getRecords().stream().map(JobLogDO::toRes).collect(Collectors.toList());
		return PageInfo.of(respList, page);
	}

}
