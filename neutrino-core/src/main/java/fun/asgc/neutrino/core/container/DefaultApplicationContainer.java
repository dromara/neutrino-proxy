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

import fun.asgc.neutrino.core.context.ApplicationContext;
import fun.asgc.neutrino.core.context.Environment;
import fun.asgc.neutrino.core.exception.ContainerInitException;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import fun.asgc.neutrino.core.util.BeanManager;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class DefaultApplicationContainer implements ApplicationContainer {
	private Environment environment;
	private BeanContainer beanContainer;
	private ApplicationContext context;

	public DefaultApplicationContainer(Environment environment) {
		this.environment = environment;
		this.init();
	}

	@Override
	public void init() throws ContainerInitException {
		this.beanContainer = new DefaultBeanContainer(environment);
		this.context = new ApplicationContext()
			.setEnvironment(environment)
			.setBeanContainer(beanContainer)
			.setApplicationConfig(environment.getConfig());
		BeanManager.setContext(this.context);
		this.beanContainer.beanList().forEach(bean -> {
			if (bean.isBoot() || !bean.isLazy()) {
				bean.newInstance(context);
			}
		});
		log.info("应用容器初始化完成");

		this.run();
	}

	private void run() {
		this.beanContainer.beanList().forEach(bean -> {
			if (bean.isBoot()) {
				if (bean.hasInstance()) {
					if (ApplicationRunner.class.isAssignableFrom(bean.getClazz())) {
						ApplicationRunner runner = (ApplicationRunner)bean.getInstance();
						runner.run(environment.getMainArgs());
					}
				}
			}
		});
	}

	@Override
	public void destroy() {
		this.beanContainer.destroy();
		this.beanContainer.beanList().forEach(bean -> bean.destroy());
		log.info("应用容器销毁");
	}
}
