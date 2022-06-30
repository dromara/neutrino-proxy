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
 * 拦截器的包装器
 * @author: aoshiguchen
 * @date: 2022/6/30
 */
@Slf4j
public class InterceptorWrapper implements Interceptor {
	private final List<Filter> filterList = new ArrayList<>();
	private final List<ResultAdvice> resultAdviceList = new ArrayList<>();
	private final List<ExceptionHandler> exceptionHandlerList = new ArrayList<>();
	private String name;

	public InterceptorWrapper() {
		this.name = this.getClass().getSimpleName();
	}

	public InterceptorWrapper(String name) {
		this.name = name;
	}

	@Override
	public void intercept(Invocation inv) {
		try {
			log.debug("拦截器：{} class:{} method:{} args:{} before", this.name, inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
			if (CollectionUtil.notEmpty(filterList)) {
				for (Filter filter : filterList) {
					if (filter.filtration(inv.getTargetClass(), inv.getTargetMethod(), inv.getArgs())) {
						return;
					}
				}
			}
			inv.invoke();
			Object result = inv.getReturnValue();
			log.debug("拦截器：{} class:{} method:{} args:{} result:{} after", this.name, inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs(), result);

			if (CollectionUtil.notEmpty(resultAdviceList)) {
				for (ResultAdvice advice : resultAdviceList) {
					result = advice.advice(inv.getTargetClass(), inv.getTargetMethod(), result);
				}
			}
			if (null == result) {
				result = TypeUtil.getDefaultValue(inv.getReturnType());
			}
			inv.setReturnValue(result);

			log.debug("拦截器：{} class:{} method:{} args:{} result:{} finished.", this.name, inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs(), result);
		} catch (Exception e) {
			log.debug("拦截器：{} class:{} method:{} args:{} exception.", this.name, inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
			if (CollectionUtil.notEmpty(exceptionHandlerList)) {
				for (ExceptionHandler handler : exceptionHandlerList) {
					if (handler.support(e)) {
						Object result = handler.handle(e);
						if (null != result) {
							inv.setReturnValue(result);
						}
						return;
					}
				}
			}
		}
	}

	/**
	 * 注册过滤器
	 * @param filter
	 */
	public synchronized void registerFilter(Filter filter) {
		Assert.notNull(filter, "过滤器不能为空!");
		this.filterList.add(filter);
	}

	/**
	 * 注册过滤器
	 * @param filterList
	 */
	public synchronized void registerFilter(List<Filter> filterList) {
		Assert.notEmpty(filterList, "过滤器不能为空!");
		this.filterList.addAll(filterList);
	}

	/**
	 * 注册结果处理器
	 * @param resultAdvice
	 */
	public synchronized void registerResultAdvice(ResultAdvice resultAdvice) {
		Assert.notNull(resultAdvice, "结果处理器不能为空!");
		this.resultAdviceList.add(resultAdvice);
	}

	/**
	 * 注册结果处理器
	 * @param resultAdviceList
	 */
	public synchronized void registerResultAdvice(List<ResultAdvice> resultAdviceList) {
		Assert.notEmpty(resultAdviceList, "结果处理器不能为空!");
		this.resultAdviceList.addAll(resultAdviceList);
	}

	/**
	 * 注册异常处理器
	 * @param exceptionHandler
	 */
	public synchronized void registerExceptionHandler(ExceptionHandler exceptionHandler) {
		Assert.notNull(exceptionHandler, "异常处理器不能为空!");
		this.exceptionHandlerList.add(exceptionHandler);
	}

	public synchronized void registerExceptionHandler(List<ExceptionHandler> exceptionHandlerList) {
		Assert.notEmpty(exceptionHandlerList, "异常处理器不能为空!");
		this.exceptionHandlerList.addAll(exceptionHandlerList);
	}
}
