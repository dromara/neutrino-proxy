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
package fun.asgc.neutrino.core.web.context;

import fun.asgc.neutrino.core.web.interceptor.HandlerInterceptor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/22
 */
public abstract class HttpContextHolder {
	private static final ThreadLocal<HttpRequestParser> httpRequestParserHolder = new ThreadLocal<>();
	private static ThreadLocal<FullHttpRequest> fullHttpRequestHolder = new ThreadLocal<>();
	private static ThreadLocal<ChannelHandlerContext> channelHandlerContextHolder = new ThreadLocal<>();
	private static ThreadLocal<List<HandlerInterceptor>> interceptorListHolder = new ThreadLocal<>();

	public static void remove() {
		httpRequestParserHolder.remove();
		fullHttpRequestHolder.remove();
		channelHandlerContextHolder.remove();
		interceptorListHolder.remove();
	}

	public static void setFullHttpRequest(FullHttpRequest request) {
		fullHttpRequestHolder.set(request);
		httpRequestParserHolder.set(HttpRequestParser.create(request));
	}

	public static void setChannelHandlerContext(ChannelHandlerContext context) {
		channelHandlerContextHolder.set(context);
	}

	public static void setInterceptorList(List<HandlerInterceptor> interceptorList) {
		interceptorListHolder.set(interceptorList);
	}

	public static List<HandlerInterceptor> getInterceptorList() {
		return interceptorListHolder.get();
	}

	public static HttpRequestParser getHttpRequestParser() {
		return httpRequestParserHolder.get();
	}

	public static FullHttpRequest getFullHttpRequest() {
		return fullHttpRequestHolder.get();
	}

	public static ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContextHolder.get();
	}

	public static void init(ChannelHandlerContext context, FullHttpRequest request) {
		setChannelHandlerContext(context);
		setFullHttpRequest(request);
	}
}
