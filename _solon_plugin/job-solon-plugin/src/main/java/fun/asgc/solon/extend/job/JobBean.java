package fun.asgc.solon.extend.job;

import fun.asgc.solon.extend.job.impl.JobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
public class JobBean implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobExecutor jobExecutor = Solon.context().getBean(JobExecutor.class);
		if (null != jobExecutor) {
			jobExecutor.execute(jobExecutionContext);
		}
	}

}
