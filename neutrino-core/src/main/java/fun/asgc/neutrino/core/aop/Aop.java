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
import fun.asgc.neutrino.core.aop.proxy.Proxy;
import fun.asgc.neutrino.core.aop.proxy.ProxyFactory;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.ClassUtil;
import fun.asgc.neutrino.core.util.LockUtil;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
@SuppressWarnings("all")
public class Aop {
	private static ProxyStrategy proxyStrategy = ProxyStrategy.AUTO;
	private static final Cache<Class<?>, Object> proxyBeanCache = new MemoryCache<>();
	private static final Cache<Class<?>/*targetType*/, Cache<Class<?>/*proxyType*/, Object>> otherProxyBeanCache = new MemoryCache<>();

	/**
	 * 被代理类型与代理类同源，即生成的代理类可以用被代理类的类型接收
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T> T get(Class<T> clazz) throws Exception {
		return (T)LockUtil.doubleCheckProcess(() -> !proxyBeanCache.containsKey(clazz),
			clazz,
			() -> proxyBeanCache.set(clazz, getProxyFactory(clazz).get(clazz)),
			() -> proxyBeanCache.get(clazz)
		);
	}

	/**
	 * 1、当且仅当被代理类型与代理类类型相等时，该方法与上面的方法等价
	 * 2、否则，生成的代理类类型是被代理类的超类，也就是只代理超类的可代理方法
	 * @param targetType
	 * @param proxyType
	 * @param <T>
	 * @param <P>
	 * @return
	 * @throws Exception
	 */
	public static <T,P> P get(Class<T> targetType, Class<P> proxyType) throws Exception {
		Assert.notNull(targetType, "被代理类类型不能为空！");
		Assert.notNull(proxyType, "代理类类型不能为空！");
		if (targetType == proxyType) {
			return get(proxyType);
		}
		if (!proxyType.isAssignableFrom(targetType)) {
			throw new RuntimeException(String.format("期望的代理类类型[%s]不是目标类型[%s]的超类!", proxyType.getName(), targetType.getName()));
		}
		return (P)LockUtil.doubleCheckProcess(
			() -> !otherProxyBeanCache.containsKey(targetType) || !otherProxyBeanCache.get(targetType).containsKey(proxyType),
			targetType,
			() -> {
				if (!otherProxyBeanCache.containsKey(targetType)) {
					otherProxyBeanCache.set(targetType, new MemoryCache<>());
				}
				otherProxyBeanCache.get(targetType).set(proxyType, getProxyFactory(targetType, proxyType).get(targetType, proxyType));
			},
			() -> otherProxyBeanCache.get(targetType).get(proxyType)
		);
	}

	/**
	 * 获取代理工厂
	 * 1、如果被代理类是一个接口，则采用jdk动态代理，减少避免不必要的字节码编译开销
	 * 2、其他情况则采用子类代理(AsgcProxy)
	 * @return
	 */
	private static ProxyFactory getProxyFactory(Class<?> clazz) {
		if (proxyStrategy == ProxyStrategy.AUTO) {
			if (ClassUtil.isInterface(clazz)) {
				return Proxy.getProxyFactory(ProxyStrategy.JDK_DYNAMIC_PROXY);
			}
			return Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY);
		}
		return Proxy.getProxyFactory(proxyStrategy);
	}

	/**
	 * 获取代理工厂
	 * 1、如果被代理类是一个接口，则采用jdk动态代理，减少避免不必要的字节码编译开销
	 * 2、其他情况则采用子类代理(AsgcProxy)
	 * @return
	 */
	private static ProxyFactory getProxyFactory(Class<?> targetType, Class<?> proxyType) {
		if (targetType == proxyType) {
			return getProxyFactory(targetType);
		}
		if (proxyStrategy == ProxyStrategy.AUTO) {
			if (ClassUtil.isInterface(proxyType)) {
				return Proxy.getProxyFactory(ProxyStrategy.JDK_DYNAMIC_PROXY);
			}
			return Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY);
		}
		return Proxy.getProxyFactory(proxyStrategy);
	}

	/**
	 * 设置代理策略
	 * @param proxyStrategy
	 */
	public static synchronized void setProxyStrategy(ProxyStrategy proxyStrategy) {
		Assert.notNull(proxyStrategy, "代理策略不能为空!");
		Aop.proxyStrategy = proxyStrategy;
	}

	/**
	 * 注册全局拦截器
	 * @param clazz
	 */
	public static void intercept(Class<? extends Interceptor> interceptorType) {
		InterceptorFactory.registerGlobalInterceptor(interceptorType);
	}

	public static void intercept(Class<?> targetType, Class<? extends Interceptor> interceptorType) {
		InterceptorFactory.registerInterceptor(targetType, interceptorType);
	}
}
