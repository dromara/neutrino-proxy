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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.JobLogListReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobLogListRes;
import fun.asgc.neutrino.proxy.server.dal.JobLogMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.JobLogDO;
import fun.asgc.solon.extend.job.IJobCallback;
import fun.asgc.solon.extend.job.JobInfo;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@Component
public class JobLogService implements IJobCallback {
	@Inject
	private MapperFactory mapperFactory;
	@Db
	private JobLogMapper jobLogMapper;

	@Override
	public void executeLog(JobInfo jobInfo, String param, Throwable throwable) {
		Integer code = 0;
		String msg = "";
		if (null == throwable) {
			msg = "执行成功";
			log.info("job[id={},name={}]执行完毕", jobInfo.getId(), jobInfo.getName());
		} else {
			log.error("job[id={},name={}]执行异常", jobInfo.getId(), jobInfo.getName(), throwable);
			msg = "执行异常:\r\n" + ExceptionUtils.getStackTrace(throwable);
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
		Page<JobLogListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
		List<JobLogDO> list = jobLogMapper.selectList(new LambdaQueryWrapper<JobLogDO>()
				.eq(null != req.getJobId(), JobLogDO::getJobId, req.getJobId())
				.orderByDesc(JobLogDO::getId)
		);
		List<JobLogListRes> respList = mapperFactory.getMapperFacade().mapAsList(list, JobLogListRes.class);
		return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
	}

}
