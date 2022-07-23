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
import fun.asgc.neutrino.core.web.param.HttpContextHolder;
import fun.asgc.neutrino.core.web.param.HttpRequestParser;
import fun.asgc.neutrino.core.web.router.DefaultHttpRouter;
import fun.asgc.neutrino.core.web.router.HttpRouteParam;
import fun.asgc.neutrino.core.web.router.HttpRouteResult;
import fun.asgc.neutrino.core.web.router.HttpRouterType;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

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
	private static volatile String httpContextPath;

	public void handle() {
		ChannelHandlerContext context = HttpContextHolder.getChannelHandlerContext();
		HttpRequestParser requestParser = HttpContextHolder.getHttpRequestParser();
		log.info("HttpRequest method:{} url:{} query:{}", requestParser.getMethod().name(), requestParser.getUrl(), requestParser.getQueryParamMap());

		String routePath = getRoutePath(requestParser.getUrl());
		HttpMethod httpMethod = HttpMethod.of(requestParser.getMethod().name());
		HttpRouteResult httpRouteResult = defaultHttpRouter.route(new HttpRouteParam().setMethod(httpMethod).setUrl(routePath));
		if (null == httpRouteResult) {
			HttpServerUtil.send404Response(context, requestParser.getUrl());
			release();
			return;
		}
		if (HttpRouterType.METHOD == httpRouteResult.getType()) {
			try {
				Object invokeResult = invoke(httpRouteResult.getInstance(), httpRouteResult.getMethod());
				String res = String.valueOf(invokeResult);
				if (null != invokeResult && !TypeUtil.isNormalBasicType(invokeResult.getClass())) {
					res = JSONObject.toJSONString(invokeResult);
				}
				FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes()));
				fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
				context.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
				return;
			} catch (Exception e) {
				log.error("Http处理异常", e);
			} finally {
				release();
			}
		} else if(HttpRouterType.PAGE == httpRouteResult.getType()) {
			// 前端页面
			String mimeType = MimeType.getMimeType(MimeType.parseSuffix(httpRouteResult.getPageLocation()));
			if (mimeType.startsWith("text/")) {
				mimeType += ";charset=utf-8";
			}
			FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(FileUtil.readBytes(httpRouteResult.getPageLocation())));
			fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, mimeType);
			fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_LANGUAGE, "zh-CN");
			fullHttpResponse.headers().add(HttpHeaderNames.SERVER, MetaDataConstant.SERVER_VS);
			fullHttpResponse.headers().add(HttpHeaderNames.DATE, new Date());
			context.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
			release();
			return;
		} else {
			// TODO
			HttpServerUtil.send404Response(context, requestParser.getUrl());
			release();
			return;
		}
	}

	private Object invoke(Object instance, Method method) throws InvocationTargetException, IllegalAccessException {
		Object[] params = new Object[method.getParameterCount()];
		if (method.getParameterCount() > 0) {
			for (int i = 0; i < method.getParameters().length; i++) {
				Parameter parameter = method.getParameters()[i];
				if (FullHttpRequest.class.isAssignableFrom(parameter.getType())) {
					params[i] = HttpContextHolder.getFullHttpRequest();
				} else if (HttpRequestParser.class.isAssignableFrom(parameter.getType())) {
					params[i] = HttpContextHolder.getHttpRequestParser();
				} else if (ChannelHandlerContext.class.isAssignableFrom(parameter.getType())) {
					params[i] = HttpContextHolder.getChannelHandlerContext();
				} else if (parameter.isAnnotationPresent(RequestBody.class)) {
					String bodyString = HttpContextHolder.getHttpRequestParser().getContentAsString();
					if (TypeUtil.isNormalBasicType(parameter.getType())) {
						params[i] = TypeUtil.conversion(bodyString, parameter.getType());
					}
					// TODO 实体转换
				} else if (parameter.isAnnotationPresent(RequestParam.class)) {
					RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
					if (StringUtil.isEmpty(requestParam.value())) {
						throw new RuntimeException(String.format("类:%s 方法:%s @RequestParam必须指定参数名称" ));
					}
					String val = HttpContextHolder.getHttpRequestParser().getParameter(requestParam.value());
					if (StringUtil.isEmpty(val)) {
						if (requestParam.required()) {
							throw new RuntimeException(String.format("类:%s 方法:%s 参数:%s 未指定" ));
						}
						params[i] = null;
					} else {
						params[i] = TypeUtil.conversion(val, parameter.getType());
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

	private String getRoutePath(String url) {
		String httpContextPath = getHttpContextPath();
		if (StringUtil.isEmpty(httpContextPath)) {
			return url;
		}
		String res = url.substring(httpContextPath.length());
		if (StringUtil.isEmpty(res)) {
			return "/";
		}
		return res;
	}

	private String getHttpContextPath() {
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == httpContextPath,
			this,
			() -> {
				httpContextPath = applicationConfig.getHttp().getContextPath();
				if (StringUtil.isEmpty(httpContextPath)) {
					httpContextPath = "/";
				}
				if (!httpContextPath.startsWith("/")) {
					httpContextPath = "/" + httpContextPath;
				}
				if (httpContextPath.endsWith("/")) {
					httpContextPath = httpContextPath.substring(0, httpContextPath.length() - 1);
				}
			},
			() -> httpContextPath
		);
	}
}
