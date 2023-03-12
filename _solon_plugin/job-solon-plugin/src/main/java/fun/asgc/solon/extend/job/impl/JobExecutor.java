package fun.asgc.solon.extend.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import fun.asgc.solon.extend.job.*;
import fun.asgc.solon.extend.job.annotation.JobHandler;
import org.noear.solon.Solon;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Job执行器
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public class JobExecutor implements IJobExecutor {
	private IJobSource jobSource;
	private ThreadPoolExecutor threadPoolExecutor;
	private Map<String, JobInfo> jobInfoMap = new ConcurrentHashMap<>();
	private SchedulerFactory schedulerFactory;
	private Scheduler scheduler;
	private Map<String, IJobHandler> jobHandlerMap = new ConcurrentHashMap<>();
	private Set<String> runJobSet = CollectionUtil.newHashSet();
	private IJobCallback jobCallback;
	private Map<String, TriggerKey> triggerKeyMap = new ConcurrentHashMap<>();

	public void start() {
		if (null == threadPoolExecutor) {
			threadPoolExecutor = new ThreadPoolExecutor(5, 20, 10L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>(), new CustomThreadFactory("SolonJob"));
		}

		List<IJobHandler> jobHandlerList = Solon.context().getBeansOfType(IJobHandler.class);
		if (!CollectionUtil.isEmpty(jobHandlerList)) {
			for (IJobHandler item : jobHandlerList) {
				JobHandler jobHandler = item.getClass().getAnnotation(JobHandler.class);
				if (null == jobHandler) {
					continue;
				}
				jobHandlerMap.put(jobHandler.name(), item);
			}
		}

		try {
			this.schedulerFactory = new StdSchedulerFactory();
			this.scheduler = schedulerFactory.getScheduler();
			this.init();
		} catch (Exception e){
			throw new RuntimeException("job初始化异常");
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
	public void init() throws Exception {
		List<JobInfo> jobInfoList = jobSource.sourceList();
		if (CollectionUtil.isEmpty(jobInfoList)) {
			return;
		}

		for (JobInfo jobInfo : jobInfoList) {
			add(jobInfo);
		}
	}

	@Override
	public void add(JobInfo jobInfo) {
		if (null == jobInfo || StrUtil.isEmpty(jobInfo.getId()) || StrUtil.isEmpty(jobInfo.getName()) ||
				StrUtil.isEmpty(jobInfo.getCron())) {
			return;
		}
		synchronized (jobInfo.getId()) {
			runJobSet.add(jobInfo.getName());
			jobInfoMap.put(jobInfo.getId(), jobInfo);

			TriggerKey triggerKey = TriggerKey.triggerKey(jobInfo.getId());
			triggerKeyMap.put(jobInfo.getId(), triggerKey);
			JobKey jobKey = new JobKey(jobInfo.getName());

			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobInfo.getCron()).withMisfireHandlingInstructionDoNothing();
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
			JobDetail jobDetail = JobBuilder.newJob(JobBean.class).withIdentity(jobKey).build();

			try {
				if (jobInfo.isEnable()) {
					scheduler.scheduleJob(jobDetail, cronTrigger);
					scheduler.start();
				}
			} catch (Exception e) {
				throw new RuntimeException(String.format("新增job[name=%s]异常", jobInfo.getName()));
			}
		}
	}

	@Override
	public void remove(String jobId) {
		JobInfo jobInfo = jobInfoMap.get(jobId);
		if (null == jobInfo) {
			return;
		}
		synchronized (jobId) {
			runJobSet.remove(jobInfo.getName());
			unscheduleJob(jobId);
		}
	}

	@Override
	public void trigger(String jobId, String param) {
		doExecute(jobId, param);
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (null == context || null == context.getTrigger()) {
			return;
		}
		String jobId = context.getTrigger().getKey().getName();
		JobInfo jobInfo = jobInfoMap.get(jobId);
		if (null == jobInfo) {
			unscheduleJob(jobId);
			return;
		}
		doExecute(jobId, jobInfo.getParam());
	}

	private void unscheduleJob(String jobId) {
		TriggerKey triggerKey = triggerKeyMap.get(jobId);
		if (null != triggerKey) {
			try {
				scheduler.unscheduleJob(triggerKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
				jobHandler.execute(param);
				if (null != jobCallback) {
					jobCallback.executeLog(jobInfo, param, null);
				}
			} catch (Throwable e) {
				jobCallback.executeLog(jobInfo, param, e);
			}
		});
	}
}
