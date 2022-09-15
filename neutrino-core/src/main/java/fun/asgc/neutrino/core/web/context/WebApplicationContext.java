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
package fun.asgc.neutrino.core.web.context;

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.bean.factory.SimpleBeanFactory;
import fun.asgc.neutrino.core.context.ApplicationContext;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.web.HttpRequestHandler;
import fun.asgc.neutrino.core.web.WebApplicationServer;
import fun.asgc.neutrino.core.web.interceptor.ControllerGlobalInterceptor;
import fun.asgc.neutrino.core.web.router.DefaultHttpRouter;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/15
 */
@Accessors(chain = true)
@Data
@Slf4j
@NonIntercept
@Component
public class WebApplicationContext implements ApplicationRunner {
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private SimpleBeanFactory applicationBeanFactory;
	/**
	 * bean工厂
	 */
	private SimpleBeanFactory webApplicationBeanFactory;

	@Init
	public void init() {
		this.webApplicationBeanFactory = new SimpleBeanFactory(applicationBeanFactory, "webApplicationBeanFactory");
		this.webApplicationBeanFactory.init();
	}

	@Destroy
	public void destroy() {
		this.webApplicationBeanFactory.destroy();
	}

	@Override
	public void run(String[] args) throws Exception {
		this.webApplicationBeanFactory.register(
			Sets.newHashSet(
				DefaultHttpRouter.class,
				HttpRequestHandler.class,
				ControllerGlobalInterceptor.class,
				WebApplicationServer.class
			)
		);
	}

}
