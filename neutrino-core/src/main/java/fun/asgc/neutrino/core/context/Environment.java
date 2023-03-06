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

package fun.asgc.neutrino.core.context;

import fun.asgc.neutrino.core.base.NvMap;
import fun.asgc.neutrino.core.base.NvMaps;
import fun.asgc.neutrino.core.base.event.SimpleApplicationEventManager;
import fun.asgc.neutrino.core.constant.MetaDataConstant;
import fun.asgc.neutrino.core.util.ArrayUtil;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.util.SystemUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fun.asgc.neutrino.core.constant.MetaDataConstant.Config.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Accessors(chain = true)
@Data
public class Environment {
	/**
	 * 启动类
	 */
	private Class<?> mainClass;
	/**
	 * 启动参数
	 */
	private String[] mainArgs;
	/**
	 * 包扫描路径
	 */
	private List<String> scanBasePackages;
	/**
	 * 横幅
	 */
	private String banner;
	/**
	 * 应用配置
	 */
	private ApplicationConfig config;
	/**
	 * 启用job
	 */
	private boolean enableJob;
	/**
	 * 运行上下文
	 */
	private SystemUtil.RunContext runContext;
	/**
	 * 默认的应用事件管理器
	 */
	private SimpleApplicationEventManager defaultApplicationEventManager;
	/**
	 * 全局配置
	 */
	private NvMap nvMap;

	public NvMap getNvMap() {
		return nvMap;
	}

	public void loadNvMap(String envKey) throws Exception {
		// 创建NvMaps实例
		NvMaps nvMaps = NvMaps.of();

		// 阶段1：设置内部默认值、别名
		nvMaps.setAlias(NEUTRINO_APPLICATION_NAME, "app.name");
		nvMaps.setKv(NEUTRINO_APPLICATION_NAME, "neutrino-app");

		nvMaps.setAlias(NEUTRINO_HTTP_ENABLE, "http.enable");
		nvMaps.setKv(NEUTRINO_HTTP_ENABLE, true);
		nvMaps.setAlias(NEUTRINO_HTTP_PORT, "http.port");
		nvMaps.setKv(NEUTRINO_HTTP_PORT, 8080);
		nvMaps.setAlias(NEUTRINO_HTTP_CONTEXT_PATH, "http.context-path");
		nvMaps.setKv(NEUTRINO_HTTP_CONTEXT_PATH, "/");
		nvMaps.setAlias(NEUTRINO_HTTP_MAX_CONTENT_LENGTH_DESC, "http.max-content-length-desc");
		nvMaps.setKv(NEUTRINO_HTTP_MAX_CONTENT_LENGTH_DESC, "/");
		nvMaps.setAlias(NEUTRINO_HTTP_STATIC_RESOURCE_LOCATIONS, "http.static-resource");

		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_MAX_FRAME_LENGTH, "protocol.max-frame-length");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_LENGTH_FIELD_OFFSET, "protocol.length-field-offset");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_LENGTH_FIELD_LENGTH, "protocol.length-field-length");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_INITIAL_BYTES_TO_STRIP, "protocol.initial-bytes-to-strip");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_LENGTH_ADJUSTMENT, "protocol.length-adjustment");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_READ_IDLE_TIME, "protocol.read-idle-time");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_WRITE_IDLE_TIME, "protocol.write-idle-time");
		nvMaps.setAlias(NEUTRINO_PROXY_PROTOCOL_ALL_IDLE_TIME_SECONDS, "protocol.all-idle-time-seconds");

		nvMaps.setAlias(NEUTRINO_PROXY_SERVER_PORT, "proxy.port");
		nvMaps.setAlias(NEUTRINO_PROXY_SERVER_SSL_PORT, "proxy.ssl-port");
		nvMaps.setAlias(NEUTRINO_PROXY_SERVER_KEY_STORE_PASSWORD, "proxy.key-store-password");
		nvMaps.setAlias(NEUTRINO_PROXY_SERVER_KEY_MANAGER_PASSWORD, "proxy.key-manager-password");
		nvMaps.setAlias(NEUTRINO_PROXY_SERVER_JKS_PATH, "proxy.jks-path");

		nvMaps.setAlias(NEUTRINO_PROXY_CLIENT_KEY_STORE_PASSWORD, "proxy.key-store-password");
		nvMaps.setAlias(NEUTRINO_PROXY_CLIENT_JKS_PATH, "proxy.jks-path");
		nvMaps.setAlias(NEUTRINO_PROXY_CLIENT_SERVER_IP, "proxy.server-ip");
		nvMaps.setAlias(NEUTRINO_PROXY_CLIENT_SERVER_PORT, "proxy.server-port");
		nvMaps.setAlias(NEUTRINO_PROXY_CLIENT_SSL_ENABLE, "proxy.ssl-enable");
		nvMaps.setAlias(NEUTRINO_PROXY_CLIENT_OBTAIN_LICENSE_INTERVAL, "proxy.obtain-license-interval");

		nvMaps.setAlias(NEUTRINO_DATA_DB_TYPE, "db.type");
		nvMaps.setAlias(NEUTRINO_DATA_DB_URL, "db.url");
		nvMaps.setAlias(NEUTRINO_DATA_DB_DRIVER_CLASS, "db.driver-class");
		nvMaps.setAlias(NEUTRINO_DATA_DB_USERNAME, "db.username");
		nvMaps.setAlias(NEUTRINO_DATA_DB_PASSWORD, "db.password");

		nvMaps.stageDone();

		// 阶段2：内部配置文件
		nvMaps.loadFile(MetaDataConstant.CLASSPATH_RESOURCE_IDENTIFIER.concat("/application.yml"));
		nvMaps.stageDone();

		// 阶段3：环境变量
		if (!StringUtil.isEmpty(envKey)) {
			nvMaps.loadEnvironmentVariable(envKey);
			nvMaps.stageDone();
		}

		// 结算4：外部配置文件
		nvMaps.loadFile("./.application.properties");
		nvMaps.stageDone();

		// 阶段5：启动参数
		nvMaps.loadMainArgs(this.mainArgs);
		nvMaps.stageDone();

		// 获取NvMap实例
		this.nvMap = nvMaps.getNvMap();
	}

	/**
	 * 参数map
	 */
	@Deprecated
	private Map<String, String> mainArgsMap = null;

	@Deprecated
	private Map<String, String> getMainArgsMap() {
		return LockUtil.doubleCheckProcessForNoException(
				() -> null == mainArgsMap,
				this,
				() -> {
					Map<String, String> map = new HashMap<>();
					if (ArrayUtil.notEmpty(mainArgs)) {
						for (String param : mainArgs) {
							if (!StringUtil.isEmpty(param)) {
								int index = param.indexOf("=");
								if (index > 0 && index < param.length() - 1) {
									String key = param.substring(0, index);
									String val = param.substring(index + 1);
									map.put(key, val);
								}
							}
						}
					}
					mainArgsMap = map;
				},
				() -> mainArgsMap
		);
	}

	@Deprecated
	private String getMainArgs(String key) {
		return getMainArgsMap().get(key);
	}

	@Deprecated
	public Integer getMainArgsForInteger(String key, Integer defaultValue) {
		String val = getMainArgs(key);
		Integer res = null;
		try {
			if (!StringUtil.isEmpty(val)) {
				res = Integer.valueOf(val);
			}
		} catch (Exception e) {
			// ignore
		}
		return (null != res) ? res : defaultValue;
	}

	@Deprecated
	public Boolean getMainArgsForBoolean(String key, Boolean defaultValue) {
		String val = getMainArgs(key);
		Boolean res = null;
		try {
			if (!StringUtil.isEmpty(val)) {
				res = Boolean.valueOf(val);
			}
		} catch (Exception e) {
			// ignore
		}
		return (null != res) ? res : defaultValue;
	}

	@Deprecated
	public String getMainArgsForString(String key, String defaultValue) {
		String res = getMainArgs(key);
		return !StringUtil.isEmpty(res) ? res : defaultValue;
	}

	@Deprecated
	public String getMainArgsForString(String key) {
		return getMainArgsForString(key, null);
	}
}
