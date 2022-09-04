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
package fun.asgc.neutrino.proxy.client.core;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.base.CustomThreadFactory;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.util.ArrayUtil;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.util.SystemUtil;
import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@NonIntercept
@Component
public class LicenseObtainService implements ApplicationRunner {
	@Autowired
	private ProxyConfig proxyConfig;
	/**
	 * 调度器
	 */
	private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory("LicenseObtain"));

	@Autowired
	private ProxyClientService proxyClientService;
	private ReentrantLock runLock = new ReentrantLock();
	private Scanner scanner = new Scanner(System.in);
	private volatile boolean isFirst;

	@Override
	public void run(String[] args) throws Exception {
		isFirst = true;
		scheduledExecutor.scheduleWithFixedDelay(() -> {
			boolean lock = runLock.tryLock();
			try {
				if (lock) {
					this.process(args);
				}
			} finally {
				if (runLock.isHeldByCurrentThread()){
					runLock.unlock();
				}
			}
		}, 0, proxyConfig.getClient().getObtainLicenseInterval(), TimeUnit.SECONDS);
	}

	public void stop() {
		scheduledExecutor.shutdown();
		log.info("licenseKey获取任务停止");
	}

	public void process(String[] args) {
		if (isFirst) {
			isFirst = true;
			SystemUtil.trySleep(2000);
		}
		String licenseKey = getLicenseKey(args);
		proxyClientService.start(licenseKey);
	}

	private String getLicenseKey(String[] args) {
		String license = "";
		if (null != args && ArrayUtil.notEmpty(args)) {
			for (String s : args) {
				if (s.startsWith("license=") && s.length() > 8) {
					license = s.substring(8).trim();
				}
			}
		}
		if (StringUtil.isEmpty(license)) {
			license = FileUtil.readContentAsString("./.neutrino-proxy.license");
		}

		while (StringUtil.isEmpty(license)) {
			System.out.print("请输入license:");
			license = scanner.next();
		}

		return license;
	}
}
