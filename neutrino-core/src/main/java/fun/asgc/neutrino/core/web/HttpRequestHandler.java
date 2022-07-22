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
			return;
		}
		if (HttpRouterType.METHOD == httpRouteResult.getType()) {
			try {
				Object invokeResult = httpRouteResult.getMethod().invoke(httpRouteResult.getInstance());
				String res = String.valueOf(invokeResult);
				if (!TypeUtil.isNormalBasicType(invokeResult.getClass())) {
					res = JSONObject.toJSONString(invokeResult);
				}
				FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes()));
				fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
				context.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
				return;
			} catch (Exception e) {
				// TODO
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
			return;
		} else {
			// TODO
			HttpServerUtil.send404Response(context, requestParser.getUrl());
			return;
		}
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
