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

import fun.asgc.neutrino.core.base.event.SimpleApplicationEventManager;
import fun.asgc.neutrino.core.util.ArrayUtil;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.util.SystemUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * 参数map
	 */
	private Map<String, String> mainArgsMap = null;

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

	private String getMainArgs(String key) {
		return getMainArgsMap().get(key);
	}

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

	public String getMainArgsForString(String key, String defaultValue) {
		String res = getMainArgs(key);
		return !StringUtil.isEmpty(res) ? res : defaultValue;
	}
}
