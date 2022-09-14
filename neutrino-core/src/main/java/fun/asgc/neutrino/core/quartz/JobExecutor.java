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
package fun.asgc.neutrino.core.quartz;

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.quartz.annotation.JobHandler;
import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Job执行器
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
public class JobExecutor implements ApplicationRunner, IJobExecutor {
	@Autowired
	private Environment environment;
	private IJobSource jobSource;
	private ThreadPoolExecutor threadPoolExecutor;
	private Map<String, JobInfo> jobInfoMap = new ConcurrentHashMap<>();
	private SchedulerFactory schedulerFactory;
	private Scheduler scheduler;
	private Map<String, IJobHandler> jobHandlerMap = new ConcurrentHashMap<>();
	private Set<String> runJobSet = Sets.newHashSet();
	private IJobCallback jobCallback;
	private Map<String, TriggerKey> triggerKeyMap = new ConcurrentHashMap<>();

	@Override
	public void run(String[] args) throws JobException {
		if (!environment.isEnableJob() || null == jobSource || null == threadPoolExecutor) {
			return;
		}

		List<IJobHandler> jobHandlerList = BeanManager.getBeanListBySuperClass(IJobHandler.class);
		if (!CollectionUtil.isEmpty(jobHandlerList)) {
			for (IJobHandler item : jobHandlerList) {
				JobHandler jobHandler = item.getClass().getAnnotation(JobHandler.class);
				if (null == jobHandler) {
					continue;
				}
				jobHandlerMap.put(jobHandler.name(), item);
				runJobSet.add(jobHandler.name());
			}
		}

		try {
			this.schedulerFactory = new StdSchedulerFactory();
			this.scheduler = schedulerFactory.getScheduler();
			this.init();
		} catch (Exception e){
			throw new JobException("job初始化异常");
		}
	}

	public void setJobSource(IJobSource jobSource) {
		this.jobSource = jobSource;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public void setJobCallback(IJobCallback jobCallback) {
		this.jobCallback = jobCallback;
	}

	@Override
	public void init() throws JobException {
		List<JobInfo> jobInfoList = jobSource.sourceList();
		if (CollectionUtil.isEmpty(jobInfoList)) {
			return;
		}

		for (JobInfo jobInfo : jobInfoList) {
			add(jobInfo);
		}
		log.info("Job初始化完成.");
	}

	@Override
	public synchronized void add(JobInfo jobInfo) throws JobException {
		if (null == jobInfo || StringUtil.isEmpty(jobInfo.getName()) || StringUtil.isEmpty(jobInfo.getCron()) || jobInfoMap.containsKey(jobInfo.getName())) {
			return;
		}
		jobInfoMap.put(jobInfo.getId(), jobInfo);

		TriggerKey triggerKey = TriggerKey.triggerKey(jobInfo.getId());
		triggerKeyMap.put(jobInfo.getId(), triggerKey);
		JobKey jobKey = new JobKey(jobInfo.getName());

		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobInfo.getCron()).withMisfireHandlingInstructionDoNothing();
		CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
		JobDetail jobDetail = JobBuilder.newJob(JobBean.class).withIdentity(jobKey).build();

		try {
			scheduler.scheduleJob(jobDetail, cronTrigger);
			scheduler.start();
		} catch (Exception e) {
			throw new RuntimeException(String.format("新增job[name=%s]异常", jobInfo.getName()));
		}
	}

	@Override
	public void remove(String jobName) {
		runJobSet.remove(jobName);
	}

	@Override
	public void trigger(String jobId, String param) {

	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (null == context || null == context.getTrigger()) {
			return;
		}
		String jobId = context.getTrigger().getKey().getName();
		JobInfo jobInfo = jobInfoMap.get(jobId);
		if (null == jobInfo) {
			TriggerKey triggerKey = triggerKeyMap.get(jobId);
			if (null != triggerKey) {
				try {
					scheduler.unscheduleJob(triggerKey);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}
		doExecute(jobId, jobInfo.getParam());
	}

	private void doExecute(String jobId, String param) {
		JobInfo jobInfo = jobInfoMap.get(jobId);
		if (null == jobInfo) {
			return;
		}
		IJobHandler jobHandler =jobHandlerMap.get(jobInfo.getName());
		if (null == jobHandler) {
			return;
		}
		threadPoolExecutor.submit(() -> {
			try {
				jobHandler.execute(jobInfo.getParam());
				if (null != jobCallback) {
					jobCallback.executeLog(jobInfo, null);
				}
			} catch (Throwable e) {
				jobCallback.executeLog(jobInfo, e);
			}
		});
	}
}
