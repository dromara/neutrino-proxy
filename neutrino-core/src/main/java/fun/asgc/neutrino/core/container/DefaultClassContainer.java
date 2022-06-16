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

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.exception.ContainerInitException;
import fun.asgc.neutrino.core.util.ClassUtil;
import fun.asgc.neutrino.core.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class DefaultClassContainer implements ClassContainer {
	private Environment environment;
	private Set<Class<?>> classes;
	private Set<Class<?>> componentClasses;

	public DefaultClassContainer(Environment environment) {
		this.environment = environment;
		this.classes = Collections.synchronizedSet(new HashSet<>());
		this.componentClasses = Collections.synchronizedSet(new HashSet<>());
		this.init();
	}

	@Override
	public void init() throws ContainerInitException {
		try {
			if (CollectionUtil.isEmpty(environment.getScanBasePackages())) {
				return;
			}
			this.classes = ClassUtil.scan(environment.getScanBasePackages());
			this.componentClasses = this.classes.stream().filter(item -> ClassUtil.isAnnotateWith(item, Component.class)).collect(Collectors.toSet());
			log.info("class容器初始化完成");
		} catch (Exception e) {
			log.error("class容器初始化异常!");
			throw new ContainerInitException(this);
		}
	}

	@Override
	public boolean hasClass(Class<?> clazz) {
		return classes.contains(clazz);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Class<?>> getComponentClasses() {
		return componentClasses;
	}

	@Override
	public void destroy() {
		log.info("class容器销毁");
	}
}
