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
package fun.asgc.neutrino.core.aop;

import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.aop.interceptor.InterceptorFactory;
import fun.asgc.neutrino.core.aop.proxy.ProxyCache;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
@Slf4j
public class Invocation {
	private Class<?> targetClass;
	private Method targetMethod;
	private Object proxy;
	private Supplier callback;
	private Object[] args;
	private List<Interceptor> interceptors;
	private volatile int index = 0;
	private Object returnValue;

	public Invocation(Long methodId, Object proxy, Supplier callback, Object... args) {
		this.targetMethod = ProxyCache.getMethod(methodId);
		this.targetClass = this.targetMethod.getDeclaringClass();
		this.proxy = proxy;
		this.callback = callback;
		this.args = args;
		this.interceptors = InterceptorFactory.getListByTargetMethod(this.targetMethod);
		this.returnValue = TypeUtil.getDefaultValue(this.targetMethod.getReturnType());
	}

	public void invoke() throws Exception {
		if (CollectionUtil.notEmpty(this.interceptors) && index < this.interceptors.size()) {
			this.interceptors.get(index++).intercept(this);
		} else {
			returnValue = callback.get();
			returnValue = TypeUtil.conversion(returnValue, this.targetMethod.getReturnType());
		}
	}

	public <T> T getReturnValue() {
		return (T)returnValue;
	}

	public Class<?> getReturnType() {
		return targetMethod.getReturnType();
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Method getTargetMethod() {
		return targetMethod;
	}

	public Object[] getArgs() {
		return args;
	}
}
