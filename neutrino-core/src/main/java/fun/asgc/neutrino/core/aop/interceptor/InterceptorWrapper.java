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
import fun.asgc.neutrino.core.util.LockUtil;

/**
 * 拦截器包装器
 * 用于支持拦截器以type或者实例形式注册
 *
 * 以type类型注册时，拦截器的实例化时机延迟到首次调用
 * @author: aoshiguchen
 * @date: 2022/7/6
 */
public class InterceptorWrapper implements Interceptor {
	/**
	 * 拦截器实例
	 */
	private volatile Interceptor instance;
	/**
	 * 拦截器类型
	 */
	private Class<? extends Interceptor> type;

	private InterceptorWrapper() {

	}

	@Override
	public void intercept(Invocation inv) throws Exception {
		// 若未实例化，则在此先实例化
		LockUtil.doubleCheckProcess(
			() -> null == instance,
			this,
			() -> InterceptorFactory.get(type)
		);
		instance.intercept(inv);
	}

	public static InterceptorWrapper create(Class<? extends Interceptor> type) {
		Assert.notNull(type, "拦截器类型不能为空!");
		InterceptorWrapper wrapper = new InterceptorWrapper();
		wrapper.type = type;
		return wrapper;
	}

	public static InterceptorWrapper create(Interceptor instance) {
		Assert.notNull(instance, "拦截器实例不能为空！");
		InterceptorWrapper wrapper = new InterceptorWrapper();
		wrapper.instance = instance;
		wrapper.type = instance.getClass();
		return wrapper;
	}
}
