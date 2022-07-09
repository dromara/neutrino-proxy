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

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.bean.SimpleBeanFactory;
import fun.asgc.neutrino.core.web.WebApplicationServer;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/9
 */
@NonIntercept
@Component
public class ExtensionServiceLoader implements ApplicationRunner {
	@Autowired
	private ApplicationConfig rootApplicationConfig;
	@Autowired
	private SimpleBeanFactory applicationBeanFactory;

	@Override
	public void run(String[] args) {
		startHttpServer();
	}

	private void startHttpServer() {
		if (null == rootApplicationConfig) {
			return;
		}
		ApplicationConfig.Http http = rootApplicationConfig.getHttp();
		if (null == http || null == http.getEnable() || !http.getEnable()) {
			return;
		}
		applicationBeanFactory.registerBean(WebApplicationServer.class);
	}
}
