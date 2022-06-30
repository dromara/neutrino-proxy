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
package fun.asgc.neutrino.core.aop.proxy;

import fun.asgc.neutrino.core.aop.Invocation;
import fun.asgc.neutrino.core.util.ArrayUtil;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class JdkDynamicProxyFactory implements ProxyFactory {

	@Override
	public <T> T get(Class<T> clazz) {
		Assert.notNull(clazz, "被代理类不能为空！");
		Assert.isTrue(canProxy(clazz), String.format("类[%s]无法被代理!", clazz.getName()));
		try {
			return doGet(clazz);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T doGet(Class<T> clazz) throws ReflectiveOperationException {
		if (ClassUtil.isInterface(clazz)) {
			return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
				new Class[]{clazz},
				new JdkDynamicProxy(null)
			);
		}
		return null;
	}

	@Override
	public boolean canProxy(Class<?> clazz) {
		return ClassUtil.isInterface(clazz) || ArrayUtil.notEmpty(clazz.getInterfaces());
	}

	@Override
	public boolean isProxyClass(Class<?> clazz) {
		return Proxy.isProxyClass(clazz);
	}

	private static class JdkDynamicProxy implements InvocationHandler {
		private Object target;
		public JdkDynamicProxy(Object target) {
			this.target = target;
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Long methodId = ProxyCache.setMethod(method);
			Invocation inv = new Invocation(methodId, proxy, () -> {
				if (null != target) {
					return method.invoke(target, args);
				}
				return null;
			}, args);
			inv.invoke();
			return inv.getReturnValue();
		}
	}
}
