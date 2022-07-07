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
import fun.asgc.neutrino.core.base.GlobalConfig;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.util.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 拦截器工厂
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class InterceptorFactory {
	private static final Cache<Class<? extends Interceptor>, Interceptor> interceptorCache = new MemoryCache<>();
	private static final List<Interceptor> globalInterceptorList = Collections.synchronizedList(new ArrayList<>());
	private static final Map<Class<?>, List<Interceptor>> classInterceptorListMap = new HashMap<>();
	private static final Map<Method, List<Interceptor>> methodInterceptorListMap = new HashMap<>();
	private static final Cache<Class<? extends Filter>, Filter> filterCache = new MemoryCache<>();
	private static final Cache<Class<? extends ResultAdvice>, ResultAdvice> resultAdviceCache = new MemoryCache<>();
	private static final Cache<Class<? extends ExceptionHandler>, ExceptionHandler> exceptionHandlerCache = new MemoryCache<>();

	static {
		registerGlobalInterceptor(InnerGlobalInterceptor.class);
	}

	/**
	 * 获取或新建缓存bean实例
	 * @param clazz
	 * @param cache
	 * @param <T>
	 * @return
	 */
	private static <T> T getOrNewCacheBean(Class<T> clazz, Cache cache) {
		return (T)LockUtil.doubleCheckProcessForNoException(() -> !cache.containsKey(clazz),
			clazz,
			() -> {
				try {
					Object obj = null;
					if (GlobalConfig.isIsContainerStartup()) {
						obj = BeanManager.getBean(clazz);
					}
					if (null == obj) {
						obj = clazz.newInstance();
					}
					cache.set(clazz, obj);
				} catch (InstantiationException|IllegalAccessException e) {
					throw e;
				}
			},
			() -> cache.get(clazz)
		);
	}

	/**
	 * 获取拦截器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T extends Interceptor> T get(Class<? extends Interceptor> clazz) {
		return (T)getOrNewCacheBean(clazz, interceptorCache);
	}

	/**
	 * 获取过滤器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	private static <T extends Filter> T getFilter(Class<? extends Filter> clazz) {
		return (T)getOrNewCacheBean(clazz, filterCache);
	}

	/**
	 * 获取过滤器列表
	 * @param classes
	 * @param <T>
	 * @return
	 */
	private static <T extends Filter> List<T> getFilterList(Class<? extends Filter>[] classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		return Stream.of(classes).map(c -> (T)getFilter(c)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取结果处理器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	private static <T extends ResultAdvice> T getResultAdvice(Class<? extends ResultAdvice> clazz) {
		return (T)getOrNewCacheBean(clazz, resultAdviceCache);
	}

	/**
	 * 获取结果处理器列表
	 * @param classes
	 * @param <T>
	 * @return
	 */
	private static <T extends ResultAdvice> List<T> getResultAdviceList(Class<? extends ResultAdvice>[] classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		return Stream.of(classes).map(c -> (T)getResultAdvice(c)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取异常处理器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	private static <T extends ExceptionHandler> T getExceptionHandler(Class<? extends ExceptionHandler> clazz) {
		return (T)getOrNewCacheBean(clazz, exceptionHandlerCache);
	}

	/**
	 * 获取异常处理器列表
	 * @param classes
	 * @param <T>
	 * @return
	 */
	private static <T extends ExceptionHandler> List<T> getExceptionHandlerList(Class<? extends ExceptionHandler>[] classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		return Stream.of(classes).map(c -> (T)getExceptionHandler(c)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取目标方法的拦截器实例集合
	 * @param targetMethod
	 * @return
	 */
	public static List<Interceptor> getListByTargetMethod(Method targetMethod) {
		Assert.notNull(targetMethod, "目标方法不能为空！");

		return LockUtil.doubleCheckProcessForNoException(() -> !methodInterceptorListMap.containsKey(targetMethod),
			targetMethod,
			() -> {
				List<Interceptor> interceptors = new ArrayList<>();
				interceptors.addAll(globalInterceptorList);
				addInterceptorByAnnotation(interceptors, targetMethod.getDeclaringClass().getAnnotation(Intercept.class));
				addInterceptorByAnnotation(interceptors, targetMethod.getAnnotation(Intercept.class));
				// 如果被代理方法所属类是一个接口，那么该接口所有继承接口链路上的注解都对该方法生效
				if (ClassUtil.isInterface(targetMethod.getDeclaringClass())) {
					List<Class<?>> interfaceList = ReflectUtil.getInterfaceAll(targetMethod.getDeclaringClass());
					if (CollectionUtil.notEmpty(interfaceList)) {
						for (Class<?> clazz : interfaceList) {
							addInterceptorByAnnotation(interceptors, clazz.getAnnotation(Intercept.class));
						}
					}
				}
				methodInterceptorListMap.put(targetMethod, interceptors);
			},
			() -> methodInterceptorListMap.get(targetMethod));
	}

	/**
	 * 根据拦截器注解，将拦截器添加到拦截器集合中
	 * @param interceptors
	 * @param intercept
	 */
	private static void addInterceptorByAnnotation(List<Interceptor> interceptors, Intercept intercept) {
		if (null == interceptors || null == intercept) {
			return;
		}
		if (ArrayUtil.notEmpty(intercept.filter()) || ArrayUtil.notEmpty(intercept.resultAdvice()) || ArrayUtil.notEmpty(intercept.exceptionHandler())) {
			DefaultInterceptor wrapper = new DefaultInterceptor();
			List<Filter> filterList = getFilterList(intercept.filter());
			List<ResultAdvice> resultAdviceList = getResultAdviceList(intercept.resultAdvice());
			List<ExceptionHandler> exceptionHandlerList = getExceptionHandlerList(intercept.exceptionHandler());
			if (CollectionUtil.notEmpty(filterList)) {
				wrapper.registerFilter(filterList);
			}
			if (CollectionUtil.notEmpty(resultAdviceList)) {
				wrapper.registerResultAdvice(resultAdviceList);
			}
			if (CollectionUtil.notEmpty(exceptionHandlerList)) {
				wrapper.registerExceptionHandler(exceptionHandlerList);
			}
			interceptors.add(wrapper);
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
		if (intercept.ignoreGlobal()) {
			interceptors.removeAll(globalInterceptorList);
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
