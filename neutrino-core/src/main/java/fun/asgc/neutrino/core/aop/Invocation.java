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

import fun.asgc.neutrino.core.aop.proxy.ProxyCache;
import fun.asgc.neutrino.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
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
	private Interceptor[] interceptors;
	private volatile int index = 0;
	private Object returnValue;

	public Invocation(Long methodId, Object proxy, Supplier callback, Object... args) {
		this.targetMethod = ProxyCache.getMethod(methodId);
		this.targetClass = this.targetMethod.getDeclaringClass();
		this.proxy = proxy;
		this.callback = callback;
		this.args = args;
		this.interceptors = getInterceptors(this.targetMethod);
	}

	/**
	 * TODO 先简单实现为 newInstance
	 * @param targetMethod
	 * @return
	 */
	public static Interceptor[] getInterceptors(Method targetMethod) {
		if (null == targetMethod) {
			return null;
		}
		Intercept intercept = targetMethod.getAnnotation(Intercept.class);
		if (null == intercept) {
			intercept = targetMethod.getDeclaringClass().getAnnotation(Intercept.class);
		}
		if (null == intercept) {
			return null;
		}
		Class<? extends Interceptor>[] classes = intercept.value();
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		Interceptor[] interceptors = new Interceptor[classes.length];
		for (int i = 0; i < classes.length; i++) {
			try {
				interceptors[i] = classes[i].newInstance();
			} catch (Exception e) {
				// ignore
			}
		}
		return interceptors;
	}

	public void invoke() {
		if (ArrayUtil.notEmpty(this.interceptors) && index < this.interceptors.length) {
			this.interceptors[index++].intercept(this);
		} else {
			returnValue = callback.get();
		}
	}

	public <T> T getReturnValue() {
		return (T)returnValue;
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
