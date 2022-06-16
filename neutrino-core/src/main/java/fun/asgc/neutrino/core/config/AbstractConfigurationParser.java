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

package fun.asgc.neutrino.core.config;

import fun.asgc.neutrino.core.annotation.Configuration;
import fun.asgc.neutrino.core.annotation.Value;
import fun.asgc.neutrino.core.constant.MetaDataConstant;
import fun.asgc.neutrino.core.exception.ConfigurationParserException;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.ReflectUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public abstract class AbstractConfigurationParser implements ConfigurationParser {

	@Override
	public <T> T parse(InputStream in, Class<T> clazz) throws ConfigurationParserException {
		Map<String, Object> config = parse2Map(in);
		return parse(config, clazz);
	}

	@Override
	public <T> T parse(Class<T> clazz) throws ConfigurationParserException {
		String fileName = defaultFileName();
		Configuration configuration = clazz.getAnnotation(Configuration.class);
		if (null != configuration && StringUtils.isNotEmpty(configuration.file())) {
			fileName = configuration.file();
		}

		try {
			InputStream in = FileUtil.getInputStream(MetaDataConstant.CLASSPATH_RESOURCE_IDENTIFIER.concat("/").concat(fileName));
			return parse(in, clazz);
		} catch (FileNotFoundException e) {
			throw new ConfigurationParserException(String.format("配置文件[%s]不存在", fileName));
		}
	}

	@Override
	public <T> T parse(Map<String, Object> config, Class<T> clazz) throws ConfigurationParserException {
		return parseProxy(config, clazz);
	}

	private  <T> T parseProxy(Map<String, Object> config, Class<?> clazz) throws ConfigurationParserException {
		T instance = null;
		try {
			instance = (T)clazz.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigurationParserException(String.format("无法实例化类：%s", clazz.getName()));
		} catch (IllegalAccessException e) {
			throw new ConfigurationParserException(String.format("没有权限访问类：%s", clazz.getName()));
		}

		String prefix = "";
		Configuration configuration = clazz.getAnnotation(Configuration.class);
		if (null != configuration) {
			prefix = configuration.prefix();
		}

		if (StringUtils.isNotEmpty(prefix)) {
			String[] prefixNodes = prefix.split("\\.");
			for (String prefixNode : prefixNodes) {
				if (config.containsKey(prefixNode) && config.get(prefixNode) instanceof Map) {
					config = (Map<String, Object>) config.get(prefixNode);
					if (CollectionUtil.isEmpty(config)) {
						return instance;
					}
				} else {
					return instance;
				}
			}
		}

		Set<Field> fields = ReflectUtil.getInheritChainDeclaredFieldSet(clazz);
		if (CollectionUtil.isEmpty(fields)) {
			return instance;
		}
		for (Field field : fields) {
			String key = field.getName();
			Value value = field.getAnnotation(Value.class);
			if (null != value && StringUtil.notEmpty(value.value())) {
				key = value.value();
			}
			boolean success = ReflectUtil.setFieldValue(field, instance, config.get(key));
			if (!success) {
				try {
					Object o = parseProxy((Map)config.get(key), field.getType());
					if (null != o) {
						ReflectUtil.setFieldValue(field, instance, o);
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return instance;
	}

	/**
	 * 解析配置为map
	 * @param in
	 * @return
	 */
	protected abstract Map<String, Object> parse2Map(InputStream in) throws ConfigurationParserException;

	/**
	 * 默认的文件名
	 * @return
	 */
	protected abstract String defaultFileName();
}
