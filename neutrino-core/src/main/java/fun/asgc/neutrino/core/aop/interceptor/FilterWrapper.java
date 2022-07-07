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

import java.lang.reflect.Method;

/**
 * filter包装器
 * @author: aoshiguchen
 * @date: 2022/7/7
 */
public class FilterWrapper implements Filter {
	/**
	 * 过滤器实例
	 */
	private volatile Filter instance;
	/**
	 * 过滤器类型
	 */
	private Class<? extends Filter> type;

	private FilterWrapper() {

	}

	private Filter getInstance() {
		// 若未实例化，则在此先实例化
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == instance,
			this,
			() -> instance = InterceptorFactory.getOrNewFilter(type),
			() -> instance
		);
	}

	@Override
	public boolean filtration(Class<?> targetClass, Method targetMethod, Object[] args) {
		return getInstance().filtration(targetClass, targetMethod, args);
	}

	public static FilterWrapper create(Class<? extends Filter> type) {
		Assert.notNull(type, "过滤器类型不能为空!");
		FilterWrapper wrapper = new FilterWrapper();
		wrapper.type = type;
		return wrapper;
	}

	public static FilterWrapper create(Filter instance) {
		Assert.notNull(instance, "过滤器实例不能为空！");
		FilterWrapper wrapper = new FilterWrapper();
		wrapper.instance = instance;
		wrapper.type = instance.getClass();
		return wrapper;
	}

	public Class<? extends Filter> getType() {
		return type;
	}
}
