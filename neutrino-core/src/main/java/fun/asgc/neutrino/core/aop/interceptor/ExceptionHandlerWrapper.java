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

import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.LockUtil;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/7
 */
public class ExceptionHandlerWrapper implements ExceptionHandler {
	/**
	 * 过滤器实例
	 */
	private volatile ExceptionHandler instance;
	/**
	 * 过滤器类型
	 */
	private Class<? extends ExceptionHandler> type;

	private ExceptionHandlerWrapper() {

	}

	private ExceptionHandler getInstance() {
		// 若未实例化，则在此先实例化
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == instance,
			this,
			() -> instance = InterceptorFactory.getOrNewExceptionHandler(type),
			() -> instance
		);
	}

	@Override
	public boolean support(Exception e) {
		return getInstance().support(e);
	}

	@Override
	public Object handle(Exception e) {
		return getInstance().handle(e);
	}

	public static ExceptionHandlerWrapper create(Class<? extends ExceptionHandler> type) {
		Assert.notNull(type, "异常处理器类型不能为空!");
		ExceptionHandlerWrapper wrapper = new ExceptionHandlerWrapper();
		wrapper.type = type;
		return wrapper;
	}

	public static ExceptionHandlerWrapper create(ExceptionHandler instance) {
		Assert.notNull(instance, "异常处理器实例不能为空！");
		ExceptionHandlerWrapper wrapper = new ExceptionHandlerWrapper();
		wrapper.instance = instance;
		wrapper.type = instance.getClass();
		return wrapper;
	}

	public Class<? extends ExceptionHandler> getType() {
		return type;
	}
}
