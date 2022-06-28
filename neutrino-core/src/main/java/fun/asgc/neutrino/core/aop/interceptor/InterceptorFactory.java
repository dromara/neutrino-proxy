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

import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.util.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 拦截器工厂
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class InterceptorFactory {
	private static final Cache<Class<? extends Interceptor>, Interceptor> interceptorCache = new MemoryCache<>();
	private static final List<Interceptor> globalInterceptorList = Collections.synchronizedList(new ArrayList<>());
	private static final Map<Method, List<Interceptor>> methodInterceptorListMap = new HashMap<>();

	static {
		registerGlobalInterceptor(InnerGlobalInterceptor.class);
	}

	public static <T extends Interceptor> T get(Class<T> clazz) {
		return (T)LockUtil.doubleCheckProcess(() -> !interceptorCache.containsKey(clazz),
			clazz,
			() -> {
				try {
					interceptorCache.set(clazz, clazz.newInstance());
				} catch (InstantiationException|IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			},
			() -> interceptorCache.get(clazz)
		);
	}

	public static List<Interceptor> getListByTargetMethod(Method targetMethod) {
		Assert.notNull(targetMethod, "目标方法不能为空！");

		return LockUtil.doubleCheckProcess(() -> !methodInterceptorListMap.containsKey(targetMethod),
			targetMethod,
			() -> {
				List<Interceptor> interceptors = new ArrayList<>();
				interceptors.addAll(globalInterceptorList);
				// 如果被代理方法所属类是一个接口，那么该接口所有继承接口链路上的注解都对该方法生效
				if (ClassUtil.isInterface(targetMethod.getDeclaringClass())) {
					List<Class<?>> interfaceList = ReflectUtil.getInterfaceAll(targetMethod.getDeclaringClass());
					if (CollectionUtil.notEmpty(interfaceList)) {
						for (Class<?> clazz : interfaceList) {
							addInterceptorByAnnotation(interceptors, clazz.getAnnotation(Intercept.class));
						}
					}
				}
				addInterceptorByAnnotation(interceptors, targetMethod.getDeclaringClass().getAnnotation(Intercept.class));
				addInterceptorByAnnotation(interceptors, targetMethod.getAnnotation(Intercept.class));
				methodInterceptorListMap.put(targetMethod, interceptors);
			},
			() -> methodInterceptorListMap.get(targetMethod));
	}

	private static void addInterceptorByAnnotation(List<Interceptor> interceptors, Intercept intercept) {
		if (null == interceptors || null == intercept) {
			return;
		}
		if (ArrayUtil.notEmpty(intercept.value())) {
			for (Class<? extends Interceptor> clazz : intercept.value()) {
				Interceptor interceptor = get(clazz);
				if (null != interceptor && !interceptors.contains(interceptor)) {
					interceptors.add(interceptor);
				}
			}
		}
		if (ArrayUtil.notEmpty(intercept.exclude())) {
			for (Class<? extends Interceptor> clazz : intercept.exclude()) {
				Interceptor interceptor = get(clazz);
				if (null != interceptor && interceptors.contains(interceptor)) {
					interceptors.remove(interceptor);
				}
			}
		}
	}

	/**
	 * 注册拦截器 - 全局
	 * @param clazz
	 */
	public static synchronized void registerGlobalInterceptor(Class<? extends  Interceptor> clazz) {
		Assert.notNull(clazz, "class不能为空!");
		Interceptor interceptor = get(clazz);
		if (!globalInterceptorList.contains(interceptor)) {
			globalInterceptorList.add(interceptor);
		}
	}
}
