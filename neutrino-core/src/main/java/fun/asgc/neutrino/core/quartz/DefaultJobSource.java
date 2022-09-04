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

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.quartz.annotation.JobHandler;
import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public class DefaultJobSource implements IJobSource {

	@Override
	public List<JobInfo> list() {
		List<IJobHandler> jobHandlerList = BeanManager.getBeanListBySuperClass(IJobHandler.class);
		if (CollectionUtil.isEmpty(jobHandlerList)) {
			return Lists.newArrayList();
		}
		List<JobInfo> jobInfoList = Lists.newArrayList();
		for (IJobHandler jobHandler : jobHandlerList) {
			JobHandler handler = jobHandler.getClass().getAnnotation(JobHandler.class);
			if (null == handler || StringUtil.isEmpty(handler.name()) || StringUtil.isEmpty(handler.cron())) {
				continue;
			}
			jobInfoList.add(new JobInfo()
				.setId(handler.name())
				.setName(handler.name())
				.setDesc(handler.desc())
				.setCron(handler.cron())
				.setParam(handler.param())
			);
		}
		return jobInfoList;
	}

}
