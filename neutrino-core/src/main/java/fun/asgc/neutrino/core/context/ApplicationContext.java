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

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.aop.interceptor.ExceptionHandler;
import fun.asgc.neutrino.core.aop.interceptor.Filter;
import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.aop.interceptor.ResultAdvice;
import fun.asgc.neutrino.core.bean.BeanFactoryAware;
import fun.asgc.neutrino.core.bean.SimpleBeanFactory;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import fun.asgc.neutrino.core.util.*;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
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
	 * 应用配置
	 */
	private ApplicationConfig applicationConfig;
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


	public ApplicationContext(Environment environment) {
		this.environment = environment;
	}

	@Override
	public synchronized void init() {
		this.lifeCycleManager.init(() -> {
			try {
				this.rootBeanFactory = new SimpleBeanFactory("rootBeanFactory");
				this.applicationBeanFactory = new SimpleBeanFactory(rootBeanFactory, "applicationBeanFactory");
				this.rootBeanFactory.registerBean(environment, "rootEnvironment");
				this.rootBeanFactory.registerBean(this, "rootApplicationContext");
				this.rootBeanFactory.registerBean(environment.getConfig(), "rootApplicationConfig");
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
	 * bean注册
	 * 所有的拦截器默认都是bean组件，没带Component注解时，默认是延迟加载的
	 */
	private void register() throws IOException, ClassNotFoundException {
		Set<Class<?>> classes = ClassUtil.scan(environment.getScanBasePackages());
		if (CollectionUtil.isEmpty(classes)) {
			return;
		}
		classes.stream()
			.filter(item -> ClassUtil.isAnnotateWith(item, Component.class)
				|| Interceptor.class.isAssignableFrom(item)
				|| Filter.class.isAssignableFrom(item)
				|| ExceptionHandler.class.isAssignableFrom(item)
				|| ResultAdvice.class.isAssignableFrom(item)
			)
			.forEach(clazz -> {
				String beanName = TypeUtil.getDefaultVariableName(clazz);
				Component component = ClassUtil.getAnnotation(clazz, Component.class);
				if (null != component && StringUtil.notEmpty(component.value())) {
					beanName = component.value();
				}
				this.applicationBeanFactory.registerBean(clazz, beanName);
			});
		if (!applicationBeanFactory.hasBean(BeanManager.class)) {
			applicationBeanFactory.registerBean(BeanManager.class);
		}
	}

	/**
	 * 应用上下文启动
	 */
	public void run() {
		init();
		List<ApplicationRunner> applicationRunnerList = this.applicationBeanFactory.getBeanList(ApplicationRunner.class);
		if (CollectionUtil.notEmpty(applicationRunnerList)) {
			applicationRunnerList.forEach(applicationRunner -> applicationRunner.run(this.environment.getMainArgs()));
 		}
	}
}
