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
package fun.asgc.neutrino.core.web.router;

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.bean.BeanWrapper;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.util.*;
import fun.asgc.neutrino.core.web.HttpMethod;
import fun.asgc.neutrino.core.web.annotation.GetMapping;
import fun.asgc.neutrino.core.web.annotation.PostMapping;
import fun.asgc.neutrino.core.web.annotation.RequestMapping;
import fun.asgc.neutrino.core.web.param.WebContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 默认的http路由器
 * @author: aoshiguchen
 * @date: 2022/7/16
 */
@Slf4j
@NonIntercept
@Component
public class DefaultHttpRouter implements HttpRouter {
	/**
	 * 路由缓存
	 */
	private Map<HttpRouteIdentity, HttpRouteInfo> routeCache = new ConcurrentHashMap<>(256);
	@Autowired
	private ApplicationConfig applicationConfig;

	@Init
	public void init() {
		// 初始化方法路由
		initMethodRoute();
		// 初始化页面路由
		initPageRoute();
	}

	/**
	 * 初始化方法路由
	 */
	private void initMethodRoute() {
		log.debug("Http路由器初始化...");
		List<BeanWrapper> beanWrapperList = WebContextHolder.getControllerBeanWrapperList();
		beanWrapperList.forEach(beanWrapper -> {
			Set<HttpMethod> methods = null;
			Set<String> paths = null;
			if (beanWrapper.getType().isAnnotationPresent(GetMapping.class)) {
				GetMapping getMapping = beanWrapper.getType().getAnnotation(GetMapping.class);
				methods = Sets.newHashSet(HttpMethod.GET);
				if (ArrayUtil.notEmpty(getMapping.value())) {
					paths = Stream.of(getMapping.value()).collect(Collectors.toSet());
				}
			} else if (beanWrapper.getType().isAnnotationPresent(PostMapping.class)) {
				PostMapping postMapping = beanWrapper.getType().getAnnotation(PostMapping.class);
				methods = Sets.newHashSet(HttpMethod.POST);
				if (ArrayUtil.notEmpty(postMapping.value())) {
					paths = Stream.of(postMapping.value()).collect(Collectors.toSet());
				}
			} else if (beanWrapper.getType().isAnnotationPresent(RequestMapping.class)) {
				RequestMapping requestMapping = beanWrapper.getType().getAnnotation(RequestMapping.class);
				if (ArrayUtil.notEmpty(requestMapping.value())) {
					paths = Stream.of(requestMapping.value()).collect(Collectors.toSet());
				}
				if (ArrayUtil.notEmpty(requestMapping.method())) {
					methods = Stream.of(requestMapping.method()).collect(Collectors.toSet());
				}
			}

			if (CollectionUtil.isEmpty(methods)){
				methods = Stream.of(HttpMethod.values()).collect(Collectors.toSet());
			}
			if (CollectionUtil.isEmpty(paths)) {
				paths = Sets.newHashSet("");
			}
			paths = paths.stream().map(p -> {
				if (p.endsWith("/")) {
					return p.substring(0, p.length() - 1);
				}
				return p;
			}).collect(Collectors.toSet());
			methodScan(beanWrapper, methods, paths);
		});
	}

	/**
	 * 初始化页面路由
	 */
	private void initPageRoute() {
		ApplicationConfig.StaticResource staticResource = applicationConfig.getHttp().getStaticResource();
		if (null == staticResource || CollectionUtil.isEmpty(staticResource.getLocations())) {
			return;
		}
		log.info("Page路由初始化...");
		staticResource.getLocations().forEach(location -> {
			// TODO
		});
	}

	private void methodScan(BeanWrapper beanWrapper, Set<HttpMethod> httpMethods, Set<String> paths) {
		Set<Method> methods = ReflectUtil.getDeclaredMethods(beanWrapper.getType());
		methods.forEach(method -> {
			Set<HttpMethod> realityHttpMethods = null;
			Set<String> subPaths = null;
			if (method.isAnnotationPresent(GetMapping.class)) {
				GetMapping getMapping = method.getAnnotation(GetMapping.class);
				if (!httpMethods.contains(HttpMethod.GET)) {
					throw new RuntimeException(String.format("Controller[type:%s, name:%s] method:%s 方法上的GetMapping注解与类的声明发生冲突!", beanWrapper.getType().getName(), beanWrapper.getName(), method.toString()));
				}
				realityHttpMethods = Sets.newHashSet(HttpMethod.GET);
				if (ArrayUtil.notEmpty(getMapping.value())) {
					subPaths = Stream.of(getMapping.value()).collect(Collectors.toSet());
				}
			} else if (method.isAnnotationPresent(PostMapping.class)) {
				PostMapping postMapping = method.getAnnotation(PostMapping.class);
				if (!httpMethods.contains(HttpMethod.POST)) {
					throw new RuntimeException(String.format("Controller[type:%s, name:%s] method:%s 方法上的PostMapping注解与类的声明发生冲突!", beanWrapper.getType().getName(), beanWrapper.getName(), method.toString()));
				}
				realityHttpMethods = Sets.newHashSet(HttpMethod.POST);
				if (ArrayUtil.notEmpty(postMapping.value())) {
					subPaths = Stream.of(postMapping.value()).collect(Collectors.toSet());
				}
			} else if (method.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
				if (ArrayUtil.notEmpty(requestMapping.method())) {
					realityHttpMethods = Stream.of(requestMapping.method()).collect(Collectors.toSet());
				}
				if (!httpMethods.containsAll(realityHttpMethods)) {
					throw new RuntimeException(String.format("Controller[type:%s, name:%s] method:%s 方法上的RequestMapping注解与类的声明发生冲突!", beanWrapper.getType().getName(), beanWrapper.getName(), method.toString()));
				}
				if (ArrayUtil.notEmpty(requestMapping.value())) {
					subPaths = Stream.of(requestMapping.value()).collect(Collectors.toSet());
				}
			} else {
				return;
			}
			if (CollectionUtil.isEmpty(realityHttpMethods)) {
				realityHttpMethods = Sets.newHashSet(httpMethods);
			}
			if (CollectionUtil.isEmpty(subPaths)) {
				subPaths = Sets.newHashSet("/");
			}
			subPaths = subPaths.stream().map(p -> {
				if (!p.startsWith("/")) {
					return "/" + p;
				}
				return p;
			}).collect(Collectors.toSet());
			addRoute(beanWrapper, method, realityHttpMethods, paths, subPaths);
		});
	}

	private void addRoute(BeanWrapper beanWrapper, Method method, Set<HttpMethod> httpMethods, Set<String> paths, Set<String> subPaths) {
		for (HttpMethod httpMethod : httpMethods) {
			for (String path : paths) {
				for (String subPath : subPaths) {
					addRoute(beanWrapper, method, httpMethod, path + subPath);
				}
			}
		}
	}

	private synchronized void addRoute(BeanWrapper beanWrapper, Method method, HttpMethod httpMethod, String path) {
		log.info("addRoute[{}#{}] httpMethod:{} path:{}", beanWrapper.getType().getName(), method.getName(), httpMethod.name(), path);
		HttpRouteIdentity identity = new HttpRouteIdentity(httpMethod, path);
		if (routeCache.containsKey(identity)) {
			throw new RuntimeException(String.format("Controller[type:%s, name:%s] method:%s 路由存在重复!", beanWrapper.getType().getName(), beanWrapper.getName(), method.getName()));
		}
		routeCache.put(identity, new HttpRouteInfo()
			.setType(HttpRouterType.METHOD)
			.setMethod(method)
			.setBeanIdentity(beanWrapper.getIdentity())
		);
	}

	private synchronized void addRoute(String path, String pageLocation) {
		log.info("addRoute path:{} file:{}", path, pageLocation);
		HttpRouteIdentity identity = new HttpRouteIdentity(HttpMethod.GET, path);
		if (routeCache.containsKey(identity)) {
			return;
		}
		routeCache.put(identity, new HttpRouteInfo()
			.setType(HttpRouterType.PAGE)
			.setPageLocation(pageLocation)
		);
	}

	@Override
	public HttpRouteResult route(HttpRouteParam httpRouteParam) {
		HttpRouteIdentity identity = new HttpRouteIdentity(httpRouteParam.getMethod(), httpRouteParam.getUrl());
		HttpRouteInfo httpRouteInfo = routeCache.get(identity);
		if (null == httpRouteInfo) {
			if (HttpMethod.GET == httpRouteParam.getMethod()) {
				ApplicationConfig.StaticResource staticResource = applicationConfig.getHttp().getStaticResource();
				if (null != staticResource && CollectionUtil.notEmpty(staticResource.getLocations())) {
					for (String location : staticResource.getLocations()) {
						String path = location + httpRouteParam.getUrl();
						if (path.endsWith("/")) {
							path += "index.html";
						}
						try (InputStream in = FileUtil.getInputStream(path)){
							if (null != in) {
								addRoute(httpRouteParam.getUrl(), path);
								httpRouteInfo = routeCache.get(identity);
								break;
							}
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
		}
		if (null == httpRouteInfo) {
			return null;
		}
		if (HttpRouterType.PAGE == httpRouteInfo.getType()) {
			return new HttpRouteResult()
				.setType(httpRouteInfo.getType())
				.setPageLocation(httpRouteInfo.getPageLocation());
		}
		return new HttpRouteResult()
			.setType(httpRouteInfo.getType())
			.setMethod(httpRouteInfo.getMethod())
			.setInstance(BeanManager.getBean(httpRouteInfo.getBeanIdentity()));
	}

	@Destroy
	public void destroy() {
		log.debug("Http路由器销毁...");
	}
}
