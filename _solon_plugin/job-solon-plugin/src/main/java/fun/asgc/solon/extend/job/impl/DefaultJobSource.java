package fun.asgc.solon.extend.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import fun.asgc.solon.extend.job.IJobHandler;
import fun.asgc.solon.extend.job.IJobSource;
import fun.asgc.solon.extend.job.annotation.JobHandler;
import fun.asgc.solon.extend.job.JobInfo;
import org.noear.solon.Solon;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public class DefaultJobSource implements IJobSource {

	@Override
	public List<JobInfo> sourceList() {
		List<IJobHandler> jobHandlerList = Solon.context().getBeansOfType(IJobHandler.class);
		if (CollectionUtil.isEmpty(jobHandlerList)) {
			return CollectionUtil.newArrayList();
		}
		List<JobInfo> jobInfoList = CollectionUtil.newArrayList();
		for (IJobHandler jobHandler : jobHandlerList) {
			JobHandler handler = jobHandler.getClass().getAnnotation(JobHandler.class);
			if (null == handler || StrUtil.isEmpty(handler.name()) || StrUtil.isEmpty(handler.cron())) {
				continue;
			}
			jobInfoList.add(new JobInfo()
				.setId(handler.name())
				.setName(handler.name())
				.setDesc(handler.desc())
				.setCron(handler.cron())
				.setParam(handler.param())
				.setEnable(true)
			);
		}
		return jobInfoList;
	}

}
