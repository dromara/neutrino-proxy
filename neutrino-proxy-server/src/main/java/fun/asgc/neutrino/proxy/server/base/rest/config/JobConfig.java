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
package fun.asgc.neutrino.proxy.server.base.rest.config;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Bean;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.base.CustomThreadFactory;
import fun.asgc.neutrino.core.quartz.DefaultJobSource;
import fun.asgc.neutrino.core.quartz.JobExecutor;
import fun.asgc.neutrino.proxy.server.service.JobLogService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务配置
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Component
public class JobConfig {
	@Autowired
	private JobLogService jobLogService;

	@Bean
	public JobExecutor jobExecutor() {
		JobExecutor executor = new JobExecutor();
		executor.setJobSource(new DefaultJobSource());
		executor.setThreadPoolExecutor(new ThreadPoolExecutor(5, 20, 10L, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(), new CustomThreadFactory("JobPool")));
		executor.setJobCallback(jobLogService);
		return executor;
	}
}
