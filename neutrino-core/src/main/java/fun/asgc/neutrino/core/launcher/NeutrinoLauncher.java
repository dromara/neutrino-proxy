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

package fun.asgc.neutrino.core.launcher;

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.annotation.NeutrinoApplication;
import fun.asgc.neutrino.core.constant.MetaDataConstant;
import fun.asgc.neutrino.core.container.ApplicationContainer;
import fun.asgc.neutrino.core.container.DefaultApplicationContainer;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class NeutrinoLauncher {
	private Environment environment;
	private ApplicationContainer applicationContainer;

	public static SystemUtil.RunContext run(final Class<?> clazz, final String[] args) {
		Assert.notNull(clazz, "启动类不能为空!");
		return new NeutrinoLauncher(clazz, args).launch();
	}

	public static void runSync(final Class<?> clazz, final String[] args) {
		run(clazz, args).sync();
		log.info("Application already stop.");
	}

	private NeutrinoLauncher(Class<?> clazz, String[]  args) {
		this.environment = new Environment()
			.setMainClass(clazz)
			.setMainArgs(args);
	}

	private SystemUtil.RunContext launch() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		environmentInit();
		this.applicationContainer = new DefaultApplicationContainer(environment);
		SystemUtil.RunContext runContext = SystemUtil.waitProcessDestroy(() -> this.applicationContainer.destroy());

		stopWatch.stop();
		printLog(environment, stopWatch);

		return runContext;
	}

	/**
	 * 环境初始化
	 */
	private void environmentInit() {
		environment.setScanBasePackages(Lists.newArrayList(TypeUtil.getPackageName(environment.getMainClass())));

		bannerProcess(environment);

		NeutrinoApplication neutrinoApplication = environment.getMainClass().getAnnotation(NeutrinoApplication.class);
		if (null != neutrinoApplication) {
			String[] scanBasePackages = neutrinoApplication.scanBasePackages();
			if (ArrayUtil.notEmpty(scanBasePackages)) {
				environment.setScanBasePackages(Lists.newArrayList(scanBasePackages));
			}
		}
		log.info("scanBasePackages: {}", environment.getScanBasePackages());

		// 加载应用配置
		environment.setConfig(ConfigUtil.getYmlConfig(ApplicationConfig.class));
		log.info("load ApplicationConfig finished.");
	}

	/**
	 * 打印启动日志
	 * @param environment
	 * @param stopWatch
	 */
	private void printLog(Environment environment, StopWatch stopWatch) {
		StringBuffer sb = new StringBuffer();
		sb.append("Started ");
		sb.append(environment.getMainClass().getName());
		sb.append(" in ");
		sb.append(stopWatch.getTotalTimeSeconds());

		try {
			double uptime = (double) ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0D;
			sb.append(" seconds (JVM running for " + uptime + ")");
		} catch (Throwable var5) {
		}
		log.info(sb.toString());
	}

	/**
	 * banner处理
	 * @param environment
	 */
	private void bannerProcess(Environment environment) {
		environment.setBanner(MetaDataConstant.DEFAULT_BANNER);
		String banner = FileUtil.readContentAsString(MetaDataConstant.APP_BANNER_FILE_PATH);
		if (StringUtil.notEmpty(banner)) {
			environment.setBanner(banner);
		}
		log.info(environment.getBanner());
	}
}
