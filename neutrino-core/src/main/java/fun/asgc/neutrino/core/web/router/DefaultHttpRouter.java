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
package fun.asgc.neutrino.core.web.router;

import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.bean.BeanWrapper;
import fun.asgc.neutrino.core.bean.SimpleBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的http路由器
 * @author: aoshiguchen
 * @date: 2022/7/16
 */
@Slf4j
@NonIntercept
@Component
public class DefaultHttpRouter implements HttpRouter {
	/**
	 * 路由缓存
	 */
	private Map<HttpRouteIdentity, HttpRouteInfo> routeCache = new ConcurrentHashMap<>(256);
	@Autowired
	private SimpleBeanFactory webApplicationBeanFactory;

	@Init
	public void init() {
		log.debug("Http路由器初始化...");
		List<BeanWrapper> beanWrapperList = webApplicationBeanFactory.beanWrapperList();
		// TODO 路由初始化
	}

	@Override
	public HttpRouteResult route(HttpRouteParam httpRouteParam) {
		HttpRouteIdentity identity = new HttpRouteIdentity(httpRouteParam.getMethod(), httpRouteParam.getUrl());
		HttpRouteInfo httpRouteInfo = routeCache.get(identity);
		if (null == httpRouteInfo) {
			return null;
		}
		if (HttpRouterType.PAGE == httpRouteInfo.getType()) {
			return new HttpRouteResult()
				.setType(httpRouteInfo.getType())
				.setPageLocation(httpRouteInfo.getPageLocation());
		}
		return new HttpRouteResult()
			.setType(httpRouteInfo.getType())
			.setMethod(httpRouteInfo.getMethod())
			.setInstance(webApplicationBeanFactory.getBean(httpRouteInfo.getBeanIdentity()));
	}

	@Destroy
	public void destroy() {
		log.debug("Http路由器销毁...");
	}

}
