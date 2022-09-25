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

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.quartz.IJobCallback;
import fun.asgc.neutrino.core.quartz.JobInfo;
import fun.asgc.neutrino.proxy.server.controller.req.JobInfoListReq;
import fun.asgc.neutrino.proxy.server.controller.req.JobLogListReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobInfoListRes;
import fun.asgc.neutrino.proxy.server.controller.res.JobLogListRes;
import fun.asgc.neutrino.proxy.server.dal.JobLogMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.JobLogDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@NonIntercept
@Component
public class JobLogService implements IJobCallback {
	@Autowired
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
		jobLogMapper.add(new JobLogDO()
				.setJobId(Integer.valueOf(jobInfo.getId()))
				.setHandler(jobInfo.getName())
				.setParam(param)
				.setCode(code)
				.setMsg(msg)
				.setAlarmStatus(0)
				.setCreateTime(new Date())
		);
	}

	public Page<JobLogListRes> page(PageQuery pageQuery, JobLogListReq req) {
		Page<JobLogListRes> page = Page.create(pageQuery);

		if(req.getJobId() != null){
			jobLogMapper.pageByJobId(page, req);
		} else {
			jobLogMapper.page(page, req);
		}
		return page;
	}

}
