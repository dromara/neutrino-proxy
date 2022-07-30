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
package fun.asgc.neutrino.core.web.test1;

import fun.asgc.neutrino.core.web.context.HttpRequestWrapper;
import fun.asgc.neutrino.core.web.context.HttpResponseWrapper;
import fun.asgc.neutrino.core.web.interceptor.HandlerInterceptor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/28
 */
@Slf4j
public class BaseAuthInterceptor implements HandlerInterceptor {

	/**
	 * 登录验证
	 * @param requestParser
	 * @param responseWrapper
	 * @param route
	 * @param targetMethod
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) throws Exception {
		// 登录验证
		String authorize = requestParser.getHeaderValue("Authorize");
		if (!"123abc".equals(authorize)) {
			throw new UserNotLoginException();
		}
		return Boolean.TRUE;
	}

	@Override
	public void postHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) throws Exception {

	}
}
