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

import fun.asgc.neutrino.core.annotation.PreLoad;
import fun.asgc.neutrino.core.base.GlobalConfig;
import fun.asgc.neutrino.core.bean.BeanFactoryAware;
import fun.asgc.neutrino.core.bean.SimpleBeanFactory;
import fun.asgc.neutrino.core.util.*;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Accessors(chain = true)
@Data
@Slf4j
public class ApplicationContext implements LifeCycle {

	/**
	 * 应用环境
	 */
	private Environment environment;
	/**
	 * 根Bean工厂
	 */
	private SimpleBeanFactory rootBeanFactory;
	/**
	 * bean工厂
	 */
	private SimpleBeanFactory applicationBeanFactory;
	/**
	 * 生命周期管理者
	 */
	private LifeCycleManager lifeCycleManager = LifeCycleManager.create();
	private Set<Class<?>> classes = new HashSet<>();

	public ApplicationContext(Environment environment) {
		this.environment = environment;
	}

	@Override
	public synchronized void init() {
		this.lifeCycleManager.init(() -> {
			try {
				GlobalConfig.setIsContainerStartup(true);
				this.classes = ClassUtil.scan(environment.getScanBasePackages());
				this.rootBeanFactory = new SimpleBeanFactory("rootBeanFactory");
				this.applicationBeanFactory = new SimpleBeanFactory(rootBeanFactory, "applicationBeanFactory");
				this.rootBeanFactory.registerBean(environment);
				this.rootBeanFactory.registerBean(this);
				this.rootBeanFactory.registerBean(environment.getConfig());
				preLoad();
				register();
				List<BeanFactoryAware> beanFactoryAwareList = this.applicationBeanFactory.getBeanList(BeanFactoryAware.class);
				if (CollectionUtil.notEmpty(beanFactoryAwareList)) {
					beanFactoryAwareList.forEach(beanFactoryAware -> beanFactoryAware.setBeanFactory(applicationBeanFactory));
				}
				this.applicationBeanFactory.init();
			} catch (Exception e) {
				log.error("应用上下文初始化异常!", e);
				System.exit(-1);
			}

			log.info("应用上下文初始化完成");
		});
	}

	@Override
	public void destroy() {
		this.lifeCycleManager.destroy(() -> {
			this.applicationBeanFactory.destroy();
			log.info("应用上下文销毁");
		});
	}

	/**
	 * 预加载
	 * @throws Exception
	 */
	private void preLoad() throws Exception {
		for (Class<?> c : this.classes) {
			if (!c.isAnnotationPresent(PreLoad.class)) {
				continue;
			}
			Set<Method> methodSet = ReflectUtil.getDeclaredMethods(c);
			if (CollectionUtil.isEmpty(methodSet)) {
				continue;
			}
			PreLoad preLoad = c.getAnnotation(PreLoad.class);
			String name = preLoad.value();
			if (StringUtil.isEmpty(name)) {
				continue;
			}
			Optional<Method> methodOptional = methodSet.stream().filter(m -> m.getName().equals(name) && Modifier.isStatic(m.getModifiers())
				&& (m.getParameterCount() == 0 ||
				(m.getParameterCount() == 1 && m.getParameters()[0].getType().isArray() && String.class.isAssignableFrom(m.getParameters()[0].getType().getComponentType()))))
				.findFirst();
			if (!methodOptional.isPresent()) {
				continue;
			}
			Method method = methodOptional.get();
			Object o = c.newInstance();
			log.debug("PreLoad execute {}#{}", c.getName(), method.getName());
			if (method.getParameterCount() == 0) {
				method.invoke(o);
			} else {
				method.invoke(o, new Object[]{this.environment.getMainArgs()});
			}
		}
	}

	/**
	 * bean注册
	 * 所有的拦截器默认都是bean组件，没带Component注解时，默认是延迟加载的
	 */
	private void register() throws IOException, ClassNotFoundException {
		classes.add(BeanManager.class);
		classes.add(ExtensionServiceLoader.class);
		applicationBeanFactory.register(classes);
	}

	/**
	 * 应用上下文启动
	 */
	public void run() {
		init();
	}
}
