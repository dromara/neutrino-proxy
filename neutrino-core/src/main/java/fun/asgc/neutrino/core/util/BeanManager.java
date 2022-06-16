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

package fun.asgc.neutrino.core.util;

import fun.asgc.neutrino.core.container.BeanContainer;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.context.ApplicationContext;
import fun.asgc.neutrino.core.context.Bean;
import fun.asgc.neutrino.core.context.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class BeanManager {
	private static volatile ApplicationContext context;

	public static synchronized void setContext(ApplicationContext ctx) {
		Assert.notNull(ctx, "ctx不能为空！");
		context = ctx;
		log.info("BeanManager初始化完成");
	}

	public static Object getBean(String name) {
		if (StringUtil.isEmpty(name)) {
			return null;
		}
		if ("environment".equals(name)) {
			return context.getEnvironment();
		} else if("applicationConfig".equals(name)) {
			return context.getApplicationConfig();
		} else if("beanContainer".equals(name)) {
			return context.getBeanContainer();
		} else if ("applicationContext".equals(name)) {
			return context;
		} else {
			if (null != context.getBeanContainer()) {
				Bean bean = context.getBeanContainer().getBean(name);
				if (null != bean) {
					if (!bean.hasInstance()) {
						bean.newInstance(context);
					}
					return bean.getInstance();
				}
			}
		}
		return null;
	}

	public static <T> T getBean(Class<T> clazz) {
		if (null == context) {
			return null;
		}
		if (clazz == Environment.class) {
			return (T)context.getEnvironment();
		} else if(clazz == ApplicationConfig.class) {
			return (T)context.getApplicationConfig();
		} else if(clazz == BeanContainer.class) {
			return (T)context.getBeanContainer();
		} else if (clazz == ApplicationContext.class) {
			return (T)context;
		} else {
			if (null != context.getBeanContainer()) {
				Bean bean = context.getBeanContainer().getBean(clazz);
				if (null != bean) {
					if (!bean.hasInstance()) {
						bean.newInstance(context);
					}
					return (T)bean.getInstance();
				}
			}
		}
		return null;
	}

	/**
	 * 根据超类获取bean集合
	 * @param superClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> getBeanListBySuperClass(Class<T> superClass) {
		if (null == context || null == context.getBeanContainer()) {
			return null;
		}
		return context.getBeanContainer().beanList().stream()
			.map(Bean::getClazz)
			.filter(item -> superClass.isAssignableFrom(item))
			.map(item -> (T)getBean(item))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

}
