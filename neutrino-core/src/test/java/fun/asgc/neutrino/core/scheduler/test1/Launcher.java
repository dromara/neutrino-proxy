/**
 * Copyright (C) 2018-2022 Zeyi information technology (Shanghai) Co., Ltd.
 * <p>
 * All right reserved.
 * <p>
 * This software is the confidential and proprietary
 * information of Zeyi Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Zeyi inc.
 */
package fun.asgc.neutrino.core.scheduler.test1;

import fun.asgc.neutrino.core.annotation.Init;
import fun.asgc.neutrino.core.annotation.NeutrinoApplication;
import fun.asgc.neutrino.core.context.NeutrinoLauncher;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author: wen.y
 * @date: 2022/8/31
 */
@NeutrinoApplication
public class Launcher {

	@Init
	public void init() throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey("1");
		JobKey jobKey = new JobKey("1");

		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		Scheduler scheduler = schedulerFactory.getScheduler();

		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/1 * * * * ?").withMisfireHandlingInstructionDoNothing();
		CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

		Class<? extends Job> jobClass_ = RemoteHttpJobBean.class;   // Class.forName(jobInfo.getJobClass());
		JobDetail jobDetail = JobBuilder.newJob(jobClass_).withIdentity(jobKey).build();

		scheduler.scheduleJob(jobDetail, cronTrigger);
		scheduler.start();
	}

	public static void main(String[] args) {
		NeutrinoLauncher.runSync(Launcher.class, args);
	}
}
