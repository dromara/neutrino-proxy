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
package fun.asgc.neutrino.core.web;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.constant.MetaDataConstant;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.util.*;
import fun.asgc.neutrino.core.web.annotation.RequestBody;
import fun.asgc.neutrino.core.web.annotation.RequestParam;
import fun.asgc.neutrino.core.web.context.HttpContextHolder;
import fun.asgc.neutrino.core.web.context.HttpRequestWrapper;
import fun.asgc.neutrino.core.web.context.HttpResponseWrapper;
import fun.asgc.neutrino.core.web.context.WebContextHolder;
import fun.asgc.neutrino.core.web.interceptor.*;
import fun.asgc.neutrino.core.web.router.DefaultHttpRouter;
import fun.asgc.neutrino.core.web.router.HttpRouteParam;
import fun.asgc.neutrino.core.web.router.HttpRouteResult;
import fun.asgc.neutrino.core.web.router.HttpRouterType;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/15
 */
@Slf4j
@NonIntercept
@Component
public class HttpRequestHandler {
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private DefaultHttpRouter defaultHttpRouter;
	private PathMatcher pathMatcher = new AntPathMatcher();

	public void handle() {
		ChannelHandlerContext context = HttpContextHolder.getChannelHandlerContext();
		HttpRequestWrapper requestParser = HttpContextHolder.getHttpRequestWrapper();
		log.info("HttpRequest method:{} url:{} query:{}", requestParser.getMethod().name(), requestParser.getUrl(), requestParser.getQueryParamMap());

		try {
			String routePath = requestParser.getRoutePath();
			HttpMethod httpMethod = HttpMethod.of(requestParser.getMethod().name());
			if (httpMethod == HttpMethod.OPTIONS) {
				HttpServerUtil.sendResponse(HttpResponseStatus.OK);
				return;
			}
			HttpRouteResult httpRouteResult = defaultHttpRouter.route(new HttpRouteParam().setMethod(httpMethod).setUrl(routePath));
			if (null == httpRouteResult) {
				HttpServerUtil.send404Response(requestParser.getUrl());
				return;
			}
			HttpContextHolder.setInterceptorList(getInterceptorsForPath(httpRouteResult.getPageRoute()));

			if (HttpRouterType.METHOD == httpRouteResult.getType()) {
				if (!preHandle(httpRouteResult.getPageRoute(), httpRouteResult.getMethod())) {
					HttpServerUtil.sendResponse(HttpResponseStatus.UNAUTHORIZED);
					return;
				}

				Object invokeResult = invoke(httpRouteResult.getInstance(), httpRouteResult.getMethod());

				if (null != WebContextHolder.getAdviceHandler()) {
					invokeResult = WebContextHolder.getAdviceHandler().advice(context, requestParser, routePath, httpRouteResult.getMethod(), invokeResult);
				}

				String res = String.valueOf(invokeResult);
				if (null != invokeResult && !TypeUtil.isNormalBasicType(invokeResult.getClass())) {
					res = JSONObject.toJSONString(invokeResult);
				}

				postHandle(httpRouteResult.getPageRoute(), httpRouteResult.getMethod());

				HttpResponseWrapper httpResponseWrapper = HttpContextHolder.getHttpResponseWrapper();
				httpResponseWrapper.setContent(Unpooled.wrappedBuffer(res.getBytes()));
				httpResponseWrapper.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
				httpResponseWrapper.writeAndFlush();
				return;
			} else if(HttpRouterType.PAGE == httpRouteResult.getType()) {
				if (!preHandle(httpRouteResult.getPageRoute(), null)) {
					return;
				}

				// 前端页面
				String mimeType = MimeType.getMimeType(MimeType.parseSuffix(httpRouteResult.getPageLocation()));
				if (mimeType.startsWith("text/")) {
					mimeType += ";charset=utf-8";
				}

				postHandle(httpRouteResult.getPageRoute(), null);

				HttpResponseWrapper httpResponseWrapper = HttpContextHolder.getHttpResponseWrapper();
				httpResponseWrapper.setContent(Unpooled.wrappedBuffer(FileUtil.readBytes(httpRouteResult.getPageLocation())));
				httpResponseWrapper.headers().add(HttpHeaderNames.CONTENT_TYPE, mimeType);
				httpResponseWrapper.headers().add(HttpHeaderNames.CONTENT_LANGUAGE, "zh-CN");
				httpResponseWrapper.headers().add(HttpHeaderNames.SERVER, MetaDataConstant.SERVER_VS);
				httpResponseWrapper.headers().add(HttpHeaderNames.DATE, new Date());
				httpResponseWrapper.writeAndFlush();
				return;
			} else {
				HttpServerUtil.send404Response(requestParser.getUrl());
				return;
			}
		} catch (Throwable e) {
			Object res = exceptionHandler(e);
			if (null != res) {
				HttpServerUtil.send200Response(res);
			}
		} finally {
			release();
		}
	}

	private boolean preHandle(String route, Method targetMethod) throws Exception {
		if (!CollectionUtil.isEmpty(HttpContextHolder.getInterceptorList())) {
			for (HandlerInterceptor handlerInterceptor : HttpContextHolder.getInterceptorList()) {
				if (!handlerInterceptor.preHandle(HttpContextHolder.getHttpRequestWrapper(), HttpContextHolder.getHttpResponseWrapper(), route, targetMethod)) {
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}

	private void postHandle(String route, Method targetMethod) throws Exception {
		if (!CollectionUtil.isEmpty(HttpContextHolder.getInterceptorList())) {
			for (HandlerInterceptor handlerInterceptor : HttpContextHolder.getInterceptorList()) {
				handlerInterceptor.postHandle(HttpContextHolder.getHttpRequestWrapper(), HttpContextHolder.getHttpResponseWrapper(), route, targetMethod);
			}
		}
	}

	private void afterCompletion(String route, Method targetMethod) {
		if (!CollectionUtil.isEmpty(HttpContextHolder.getInterceptorList())) {
			for (HandlerInterceptor handlerInterceptor : HttpContextHolder.getInterceptorList()) {
				handlerInterceptor.afterCompletion(HttpContextHolder.getHttpRequestWrapper(), HttpContextHolder.getHttpResponseWrapper(), route, targetMethod);
			}
		}
	}

	private List<HandlerInterceptor> getInterceptorsForPath(String lookupPath) {
		List<HandlerInterceptor> result = new ArrayList<HandlerInterceptor>();
		InterceptorRegistry interceptorRegistry = WebContextHolder.getInterceptorRegistry();
		if (CollectionUtil.isEmpty(interceptorRegistry.getInterceptors())) {
			return result;
		}
		for (Object interceptor : interceptorRegistry.getInterceptors()) {
			if (interceptor instanceof HandlerInterceptor) {
				result.add((HandlerInterceptor) interceptor);
			} else if(interceptor instanceof MappedInterceptor) {
				MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
				if (mappedInterceptor.matches(lookupPath, pathMatcher)) {
					result.add(mappedInterceptor.getInterceptor());
				}
			}
		}
		return result;
	}

	private Object exceptionHandler(Throwable e) {
		ExceptionHandlerRegistry exceptionHandlerRegistry = WebContextHolder.getExceptionHandlerRegistry();
		if (CollectionUtil.isEmpty(exceptionHandlerRegistry.getExceptionHandlerList())) {
			log.error("Http处理异常", e);
			HttpServerUtil.send500Response(e);
			return null;
		}
		for (RestControllerExceptionHandler exceptionHandler : exceptionHandlerRegistry.getExceptionHandlerList()) {
			if (exceptionHandler.support(e)) {
				return exceptionHandler.handle(HttpContextHolder.getHttpRequestWrapper(), HttpContextHolder.getHttpResponseWrapper(), e);
			}
		}
		log.error("Http处理异常", e);
		HttpServerUtil.send500Response(e);
		return null;
	}

	private Object invoke(Object instance, Method method) throws InvocationTargetException, IllegalAccessException {
		Object[] params = new Object[method.getParameterCount()];
		if (method.getParameterCount() > 0) {
			for (int i = 0; i < method.getParameters().length; i++) {
				Parameter parameter = method.getParameters()[i];
				if (FullHttpRequest.class.isAssignableFrom(parameter.getType())) {
					params[i] = HttpContextHolder.getFullHttpRequest();
				} else if (HttpRequestWrapper.class.isAssignableFrom(parameter.getType())) {
					params[i] = HttpContextHolder.getHttpRequestWrapper();
				} else if (ChannelHandlerContext.class.isAssignableFrom(parameter.getType())) {
					params[i] = HttpContextHolder.getChannelHandlerContext();
				} else if (parameter.isAnnotationPresent(RequestBody.class)) {
					String bodyString = HttpContextHolder.getHttpRequestWrapper().getContentAsString();
					if (TypeUtil.isNormalBasicType(parameter.getType())) {
						params[i] = TypeUtil.conversion(bodyString, parameter.getType());
					} else {
						params[i] = JSONObject.parseObject(bodyString, parameter.getType());
					}
				} else if (parameter.isAnnotationPresent(RequestParam.class)) {
					RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
					if (StringUtil.isEmpty(requestParam.value())) {
						throw new RuntimeException(String.format("类:%s 方法:%s @RequestParam必须指定参数名称" ));
					}
					String val = HttpContextHolder.getHttpRequestWrapper().getParameter(requestParam.value());
					if (StringUtil.isEmpty(val)) {
						if (requestParam.required()) {
							throw new RuntimeException(String.format("类:%s 方法:%s 参数:%s 未指定" ));
						}
						params[i] = null;
					} else {
						params[i] = TypeUtil.conversion(val, parameter.getType());
					}
				} else if (!TypeUtil.isNormalBasicType(parameter.getType())) {
					Set<Field> fields = ReflectUtil.getDeclaredFields(parameter.getType());
					if (CollectionUtil.notEmpty(fields)) {
						try {
							Object obj = parameter.getType().newInstance();
							for (Field field : fields) {
								String name = field.getName();
								Object value = TypeUtil.conversion(HttpContextHolder.getHttpRequestWrapper().getParameter(name), field.getType());
								ReflectUtil.setFieldValue(field, obj, value);
							}
							params[i] = obj;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
 				}
			}
		}
		Object invokeResult = method.invoke(instance, params);
		return invokeResult;
	}

	private void release() {
		HttpContextHolder.remove();
	}
}
