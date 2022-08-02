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
package fun.asgc.neutrino.proxy.server.base.rest.interceptor;

import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.web.context.HttpContextHolder;
import fun.asgc.neutrino.core.web.context.HttpRequestWrapper;
import fun.asgc.neutrino.core.web.context.HttpResponseWrapper;
import fun.asgc.neutrino.core.web.interceptor.HandlerInterceptor;
import fun.asgc.neutrino.proxy.server.base.rest.*;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.service.UserService;
import fun.asgc.neutrino.proxy.server.util.HttpUtil;

import java.lang.reflect.Method;

/**
 * 鉴权拦截器
 * @author: aoshiguchen
 * @date: 2022/7/30
 */
public class BaseAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) throws Exception {
		SystemContext systemContext = new SystemContext();
		SystemContextHolder.set(systemContext);
		systemContext.setIp(HttpUtil.getIP(HttpContextHolder.getChannelHandlerContext(), requestParser));

		Authorization authorization = targetMethod.getAnnotation(Authorization.class);
		if (null == authorization || authorization.login()) {
			String authorize = requestParser.getHeaderValue("Authorize");
			if (StringUtil.isEmpty(authorize)) {
				throw ServiceException.create(ExceptionConstant.USER_NOT_LOGIN);
			}
			UserDO userDO = BeanManager.getBean(UserService.class).findByToken(authorize);
			if (null == userDO) {
				throw ServiceException.create(ExceptionConstant.USER_NOT_LOGIN);
			}
			systemContext.setToken(authorize);
			systemContext.setUser(userDO);
		}

		return true;
	}

	@Override
	public void afterCompletion(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) {
		SystemContextHolder.remove();
	}
}
