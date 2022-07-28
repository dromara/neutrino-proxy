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

import fun.asgc.neutrino.core.aop.support.Async;
import fun.asgc.neutrino.core.aop.support.AsyncInterceptor;
import fun.asgc.neutrino.core.aop.support.Singleton;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.aop.support.SingletonInterceptor;
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
					if (GlobalConfig.isContainerStartup()) {
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
	public static <T extends Interceptor> T getOrNew(Class<? extends Interceptor> clazz) {
		return (T)getOrNewCacheBean(clazz, interceptorCache);
	}

	/**
	 * 获取过滤器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T extends Filter> T getOrNewFilter(Class<? extends Filter> clazz) {
		return (T)getOrNewCacheBean(clazz, filterCache);
	}

	/**
	 * 获取过滤器列表
	 * @param classes
	 * @param <T>
	 * @return
	 */
	public static <T extends Filter> List<T> getOrNewFilterList(Class<? extends Filter>[] classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		return Stream.of(classes).map(c -> (T)getOrNewFilter(c)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取结果处理器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T extends ResultAdvice> T getOrNewResultAdvice(Class<? extends ResultAdvice> clazz) {
		return (T)getOrNewCacheBean(clazz, resultAdviceCache);
	}

	/**
	 * 获取结果处理器列表
	 * @param classes
	 * @param <T>
	 * @return
	 */
	public static <T extends ResultAdvice> List<T> getOrNewResultAdviceList(Class<? extends ResultAdvice>[] classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		return Stream.of(classes).map(c -> (T)getOrNewResultAdvice(c)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取异常处理器实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T extends ExceptionHandler> T getOrNewExceptionHandler(Class<? extends ExceptionHandler> clazz) {
		return (T)getOrNewCacheBean(clazz, exceptionHandlerCache);
	}

	/**
	 * 获取异常处理器列表
	 * @param classes
	 * @param <T>
	 * @return
	 */
	public static <T extends ExceptionHandler> List<T> getOrNewExceptionHandlerList(Class<? extends ExceptionHandler>[] classes) {
		if (ArrayUtil.isEmpty(classes)) {
			return null;
		}
		return Stream.of(classes).map(c -> (T)getOrNewExceptionHandler(c)).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取拦截器实例
	 * @param clazz
	 * @return
	 */
	public static Interceptor get(Class<? extends Interceptor> clazz) {
		return InterceptorWrapper.create(clazz);
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

			List<Interceptor> interceptors = new ArrayList<>();
			interceptors.addAll(globalInterceptorList);
			if (classInterceptorListMap.containsKey(targetMethod.getDeclaringClass())) {
				interceptors.addAll(classInterceptorListMap.get(targetMethod.getDeclaringClass()));
			}
			addInterceptorByAnnotation(interceptors, ClassUtil.getAnnotation(targetMethod.getDeclaringClass(), Intercept.class));
			addInterceptorByAnnotation(interceptors, ClassUtil.getAnnotation(targetMethod, Intercept.class));
			if (CollectionUtil.notEmpty(methodInterceptorListMap.get(targetMethod))) {
				interceptors.addAll(methodInterceptorListMap.get(targetMethod));
			}
			// 如果被代理方法所属类是一个接口，那么该接口所有继承接口链路上的注解都对该方法生效
			if (ClassUtil.isInterface(targetMethod.getDeclaringClass())) {
				List<Class<?>> interfaceList = ReflectUtil.getInterfaceAll(targetMethod.getDeclaringClass());
				if (CollectionUtil.notEmpty(interfaceList)) {
					for (Class<?> clazz : interfaceList) {
						addInterceptorByAnnotation(interceptors, ClassUtil.getAnnotation(clazz, Intercept.class));
					}
				}
			}
			Singleton singleton = ClassUtil.getSingletonAnnotation(targetMethod);
			if (null != singleton && singleton.value()) {
				SingletonInterceptor interceptor = getOrNew(SingletonInterceptor.class);
				interceptors.add(interceptor);
			}
			Async async = ClassUtil.getAsyncAnnotation(targetMethod);
			if (null != async && async.value()) {
				AsyncInterceptor interceptor = getOrNew(AsyncInterceptor.class);
				interceptors.add(interceptor);
			}
			return interceptors;
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
			DefaultInterceptor interceptor = new DefaultInterceptor();
			List<Filter> filterList = getFilterList(intercept.filter());
			List<ResultAdvice> resultAdviceList = getResultAdviceList(intercept.resultAdvice());
			List<ExceptionHandler> exceptionHandlerList = getExceptionHandlerList(intercept.exceptionHandler());
			if (CollectionUtil.notEmpty(filterList)) {
				interceptor.registerFilter(filterList);
			}
			if (CollectionUtil.notEmpty(resultAdviceList)) {
				interceptor.registerResultAdvice(resultAdviceList);
			}
			if (CollectionUtil.notEmpty(exceptionHandlerList)) {
				interceptor.registerExceptionHandler(exceptionHandlerList);
			}
			interceptors.add(interceptor);
		}
		if (ArrayUtil.notEmpty(intercept.value())) {
			for (Class<? extends Interceptor> clazz : intercept.value()) {
				Interceptor interceptor = getOrNew(clazz);
				if (null != interceptor && !interceptors.contains(interceptor)) {
					interceptors.add(interceptor);
				}
			}
		}
		if (ArrayUtil.notEmpty(intercept.exclude())) {
			for (Class<? extends Interceptor> clazz : intercept.exclude()) {
				Interceptor interceptor = get(clazz);
				removeInterceptor(interceptors, interceptor);
			}
		}
		if (intercept.ignoreGlobal()) {
			removeInterceptor(interceptors, globalInterceptorList);
		}
	}

	/**
	 * 注册拦截器 - 全局
	 * @param clazz
	 */
	public static synchronized void registerGlobalInterceptor(Class<? extends  Interceptor> clazz) {
		Assert.notNull(clazz, "class不能为空!");
		Interceptor interceptor = get(clazz);
		if (!containsInterceptor(globalInterceptorList, interceptor)) {
			globalInterceptorList.add(interceptor);
		}
	}

	/**
	 * 注册类的拦截器
	 * @param targetType
	 * @param interceptorType
	 */
	public static synchronized void registerInterceptor(Class<?> targetType, Class<? extends Interceptor> interceptorType) {
		Assert.notNull(targetType, "目标类型不能为空不能为空!");
		Assert.notNull(interceptorType, "拦截器类型不能为空不能为空!");
		if (!classInterceptorListMap.containsKey(targetType)) {
			classInterceptorListMap.put(targetType, new ArrayList<>());
		}
		Interceptor interceptor = get(interceptorType);
		if (!containsInterceptor(classInterceptorListMap.get(targetType), interceptor)) {
			classInterceptorListMap.get(targetType).add(interceptor);
		}
	}

	/**
	 * 注册方法的拦截器
	 * @param targetMethod
	 * @param interceptorType
	 */
	public static synchronized void registerInterceptor(Method targetMethod, Class<? extends Interceptor> interceptorType) {
		Assert.notNull(targetMethod, "目标方法不能为空不能为空!");
		Assert.notNull(interceptorType, "拦截器类型不能为空不能为空!");
		if (!methodInterceptorListMap.containsKey(targetMethod)) {
			methodInterceptorListMap.put(targetMethod, new ArrayList<>());
		}
		Interceptor interceptor = get(interceptorType);
		if (!containsInterceptor(methodInterceptorListMap.get(targetMethod), interceptor)) {
			methodInterceptorListMap.get(targetMethod).add(interceptor);
		}
	}


	/**
	 * 删除指定拦截器
	 * @param list
	 * @param removeList
	 */
	private static void removeInterceptor(List<Interceptor> list, List<Interceptor> removeList) {
		if (CollectionUtil.isEmpty(list) || CollectionUtil.isEmpty(removeList)) {
			return;
		}
		removeList.forEach(item -> removeInterceptor(list, item));
	}

	private static boolean containsInterceptor(List<Interceptor> list, Interceptor interceptor) {
		if (CollectionUtil.isEmpty(list) || null == interceptor) {
			return false;
		}
		Iterator<Interceptor> iter = list.iterator();
		while (iter.hasNext()) {
			Interceptor item = iter.next();
			if (typeEquals(item, interceptor)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除指定拦截器
	 * @param list
	 * @param interceptor
	 */
	private static void removeInterceptor(List<Interceptor> list, Interceptor interceptor) {
		if (CollectionUtil.isEmpty(list) || null == interceptor) {
			return;
		}
		Iterator<Interceptor> iter = list.iterator();
		while (iter.hasNext()) {
			Interceptor item = iter.next();
			if (typeEquals(item, interceptor)) {
				iter.remove();
				return;
			}
		}
	}

	private static boolean typeEquals(Interceptor a, Interceptor b) {
		if (a == b) {
			return true;
		}
		if (null == a || null == b) {
			return false;
		}
		Class<?> type1 = a.getClass();
		Class<?> type2 = b.getClass();
		if (a instanceof InterceptorWrapper) {
			type1 = ((InterceptorWrapper)a).getType();
		}
		if (b instanceof InterceptorWrapper) {
			type2 = ((InterceptorWrapper)b).getType();
		}
		return type1 == type2;
	}

	private static boolean typeEquals(Filter a, Filter b) {
		if (a == b) {
			return true;
		}
		if (null == a || null == b) {
			return false;
		}
		Class<?> type1 = a.getClass();
		Class<?> type2 = b.getClass();
		if (a instanceof FilterWrapper) {
			type1 = ((FilterWrapper)a).getType();
		}
		if (b instanceof FilterWrapper) {
			type2 = ((FilterWrapper)b).getType();
		}
		return type1 == type2;
	}

	private static boolean typeEquals(ResultAdvice a, ResultAdvice b) {
		if (a == b) {
			return true;
		}
		if (null == a || null == b) {
			return false;
		}
		Class<?> type1 = a.getClass();
		Class<?> type2 = b.getClass();
		if (a instanceof ResultAdviceWrapper) {
			type1 = ((ResultAdviceWrapper)a).getType();
		}
		if (b instanceof ResultAdviceWrapper) {
			type2 = ((ResultAdviceWrapper)b).getType();
		}
		return type1 == type2;
	}

	private static boolean typeEquals(ExceptionHandler a, ExceptionHandler b) {
		if (a == b) {
			return true;
		}
		if (null == a || null == b) {
			return false;
		}
		Class<?> type1 = a.getClass();
		Class<?> type2 = b.getClass();
		if (a instanceof ExceptionHandlerWrapper) {
			type1 = ((ExceptionHandlerWrapper)a).getType();
		}
		if (b instanceof ExceptionHandlerWrapper) {
			type2 = ((ExceptionHandlerWrapper)b).getType();
		}
		return type1 == type2;
	}
}
