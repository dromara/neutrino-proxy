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
package fun.asgc.neutrino.core.scheduler.test1;

import fun.asgc.neutrino.core.bean.BeanWrapper;
import fun.asgc.neutrino.core.util.DateUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 * @author xuxueli 2015-12-17 18:20:34
 */
public class RemoteHttpJobBean implements Job {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//		try {
//			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
//			MutablePropertyValues pvs = new MutablePropertyValues();
//			pvs.addPropertyValues(context.getScheduler().getContext());
//			pvs.addPropertyValues(context.getMergedJobDataMap());
//			bw.setPropertyValues(pvs, true);
//		} catch (SchedulerException var4) {
//			throw new JobExecutionException(var4);
//		}
//
//		this.executeInternal(context);
		System.out.println("job执行：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
	}

	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

//		// load jobId
//		JobKey jobKey = context.getTrigger().getJobKey();
//		Integer jobId = Integer.valueOf(jobKey.getName());
//
//		// trigger
//		JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null);
	}

}
