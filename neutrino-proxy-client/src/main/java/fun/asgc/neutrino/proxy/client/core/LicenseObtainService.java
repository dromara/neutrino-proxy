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

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.base.CustomThreadFactory;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.util.ArrayUtil;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.client.config.CustomConfig;
import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * license获取服务
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@NonIntercept
@Component
public class LicenseObtainService {
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
	@Autowired
	private Environment environment;

	public void start() {
		scheduledExecutor.scheduleWithFixedDelay(() -> {
			boolean lock = runLock.tryLock();
			try {
				if (lock) {
					this.process(this.environment.getMainArgs());
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
		CustomConfig customConfig = getCustomConfigByCliParams(args);
		proxyConfig.getClient().setJksPath(customConfig.getJksPath());
		proxyConfig.getClient().setServerIp(customConfig.getServerIp());
		proxyConfig.getClient().setServerPort(customConfig.getServerPort());
		proxyConfig.getClient().setSslEnable(customConfig.getSslEnable());
		proxyConfig.setLicenseKey(customConfig.getLicenseKey());
		proxyConfig.setCustomConfig(customConfig);
		proxyClientService.start();
	}

	private CustomConfig getCustomConfigByCliParams(String[] args) {
		CustomConfig customConfig = new CustomConfig();
		// 默认无需输入
		customConfig.setJksPath(environment.getMainArgsForString("jksPath", proxyConfig.getClient().getJksPath()));
		customConfig.setServerIp(environment.getMainArgsForString("serverIp", proxyConfig.getClient().getServerIp()));
		customConfig.setServerPort(environment.getMainArgsForInteger("serverPort", proxyConfig.getClient().getServerPort()));
		customConfig.setSslEnable(environment.getMainArgsForBoolean("sslEnable", proxyConfig.getClient().getSslEnable()));
		customConfig.setLicenseKey(environment.getMainArgsForString("licenseKey"));
		// 启动参数指定了license，则无需后续处理
		if (StringUtil.notEmpty(customConfig.getLicenseKey())) {
//			FileUtil.write("./.neutrino-proxy-client.json", JSONObject.toJSONString(customConfig, SerializerFeature.PrettyFormat));
			return customConfig;
		}

		// 启动参数未指定license，则优先读取外部配置
		String config = FileUtil.readContentAsString("./.neutrino-proxy-client.json");
		if (StringUtil.notEmpty(config)) {
			try {
				customConfig = JSONObject.parseObject(config, CustomConfig.class);
			} catch (Exception e) {
				log.error("file '.neutrino-proxy-client.json' config exception!", e);
			}
			if (StringUtil.notEmpty(customConfig.getLicenseKey())) {
//				FileUtil.write("./.neutrino-proxy-client.json", JSONObject.toJSONString(customConfig, SerializerFeature.PrettyFormat));
				return customConfig;
			}
		}

		String license = "";
		while (StringUtil.isEmpty(license)) {
			System.out.print("Please input license:");
			license = scanner.next();
		}
		customConfig.setLicenseKey(license);
//		FileUtil.write("./.neutrino-proxy-client.json", JSONObject.toJSONString(customConfig, SerializerFeature.PrettyFormat));

		return customConfig;
	}

	/**
	 * 获取命令行参数
	 * @param args
	 * @return
	 */
	private Map<String, String> getCliParams(String[] args) {
		Map<String, String> res = new HashMap<>();
		if (ArrayUtil.notEmpty(args)) {
			for (String item : args) {
				if (StringUtil.isEmpty(item) || !item.contains("=")) {
					continue;
				}
				int index = item.indexOf("=");
				if (index <= 0 || index == item.length() - 1) {
					continue;
				}
				res.put(item.substring(0, index), item.substring(index + 1));
			}
		}

		return res;
	}
}
