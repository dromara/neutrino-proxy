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

package fun.asgc.neutrino.core.container;

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Order;
import fun.asgc.neutrino.core.bean.BeanWrapper;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.exception.ContainerInitException;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import fun.asgc.neutrino.core.util.ClassUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class DefaultBeanContainer implements BeanContainer {
	private Environment environment;
	private ClassContainer classContainer;
	private Cache<String, BeanWrapper> nameBeanCache;
	private Cache<Class<?>, BeanWrapper> classBeanCache;
	private List<BeanWrapper> beans;

	public DefaultBeanContainer(Environment environment) {
		this.environment = environment;
		this.nameBeanCache = new MemoryCache<>();
		this.classBeanCache = new MemoryCache<>();
		this.beans = new ArrayList<>();
		this.init();
	}

	@Override
	public boolean hasBean(Class<?> clazz) {
		return classBeanCache.containsKey(clazz);
	}

	@Override
	public boolean hasBean(String name) {
		return nameBeanCache.containsKey(name);
	}

	@Override
	public BeanWrapper getBean(Class<?> clazz) {
		return classBeanCache.get(clazz);
	}

	@Override
	public BeanWrapper getBean(String name) {
		return nameBeanCache.get(name);
	}

	@Override
	public void init() throws ContainerInitException {
		this.classContainer = new DefaultClassContainer(environment);
		this.classContainer.getComponentClasses().forEach(clazz -> {
			String name = clazz.getName();
			Component component = ClassUtil.getAnnotation(clazz, Component.class);
			if (null != component && StringUtil.notEmpty(component.value())) {
				name = component.value();
			}
			Order order = ClassUtil.getAnnotation(clazz, Order.class);
			int orderValue = Integer.MAX_VALUE;
			if (null != order) {
				orderValue = order.value();
			}

			addBean(new BeanWrapper()
				.setType(clazz)
				.setName(name)
				.setComponent(component)
				.setOrder(orderValue)
			);

		});

		if (!classBeanCache.isEmpty()) {
			beans = Lists.newArrayList(classBeanCache.values());
			Collections.sort(beans, Comparator.comparingInt(BeanWrapper::getOrder));
		}

		log.info("bean容器初始化完成");
	}

	@Override
	public void destroy() {
		this.classContainer.destroy();
		log.info("bean容器销毁");
	}

	@Override
	public void addBean(BeanWrapper bean) {
		if (null == bean) {
			return;
		}
		// TODO 暂时由编码时规避名字冲突
		if (ApplicationRunner.class.isAssignableFrom(bean.getType())) {
			bean.setOrder(Integer.MIN_VALUE);
			bean.setBoot(true);
		}
		classBeanCache.set(bean.getType(), bean);
		nameBeanCache.set(bean.getName(), bean);
	}

	@Override
	public List<BeanWrapper> beanList() {
		return beans;
	}
}
