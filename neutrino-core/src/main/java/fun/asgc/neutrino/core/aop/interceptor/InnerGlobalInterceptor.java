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
package fun.asgc.neutrino.core.aop.interceptor;

import fun.asgc.neutrino.core.aop.Invocation;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部的全局拦截器
 * @author: aoshiguchen
 * @date: aoshiguchen
 */
@Slf4j
public class InnerGlobalInterceptor implements Interceptor {
	/**
	 * 拦截器包装器
	 */
	private static final InterceptorWrapper interceptorWrapper = new InterceptorWrapper(InnerGlobalInterceptor.class.getSimpleName());

	public InnerGlobalInterceptor() {
		System.out.println("aaaaa");
	}

	@Override
	public void intercept(Invocation inv) throws Exception {
		interceptorWrapper.intercept(inv);
	}

	/**
	 * 注册过滤器
	 * @param filter
	 */
	public static synchronized void registerFilter(Filter filter) {
		interceptorWrapper.registerFilter(filter);
	}

	/**
	 * 注册过滤器
	 * @param filterList
	 */
	public static synchronized void registerFilter(List<Filter> filterList) {
		interceptorWrapper.registerFilter(filterList);
	}

	/**
	 * 注册结果处理器
	 * @param resultAdvice
	 */
	public static synchronized void registerResultAdvice(ResultAdvice resultAdvice) {
		interceptorWrapper.registerResultAdvice(resultAdvice);
	}

	/**
	 * 注册结果处理器
	 * @param resultAdviceList
	 */
	public static synchronized void registerResultAdvice(List<ResultAdvice> resultAdviceList) {
		interceptorWrapper.registerResultAdvice(resultAdviceList);
	}

	/**
	 * 注册异常处理器
	 * @param exceptionHandler
	 */
	public static synchronized void registerExceptionHandler(ExceptionHandler exceptionHandler) {
		interceptorWrapper.registerExceptionHandler(exceptionHandler);
	}

	public static synchronized void registerExceptionHandler(List<ExceptionHandler> exceptionHandlerList) {
		interceptorWrapper.registerExceptionHandler(exceptionHandlerList);
	}
}
