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
package fun.asgc.neutrino.core.web.interceptor;

import fun.asgc.neutrino.core.web.PathMatcher;
import fun.asgc.neutrino.core.web.context.HttpRequestWrapper;
import fun.asgc.neutrino.core.web.context.HttpResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Method;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/27
 */
public class MappedInterceptor {
	private final String[] includePatterns;
	private final String[] excludePatterns;
	private final HandlerInterceptor interceptor;
	private PathMatcher pathMatcher;

	public MappedInterceptor(String[] includePatterns, HandlerInterceptor interceptor) {
		this(includePatterns, null, (HandlerInterceptor)interceptor);
	}

	public MappedInterceptor(String[] includePatterns, String[] excludePatterns, HandlerInterceptor interceptor) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.interceptor = interceptor;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	public String[] getPathPatterns() {
		return this.includePatterns;
	}

	public HandlerInterceptor getInterceptor() {
		return this.interceptor;
	}

	public boolean matches(String lookupPath, PathMatcher pathMatcher) {
		PathMatcher pathMatcherToUse = this.pathMatcher != null ? this.pathMatcher : pathMatcher;
		String[] var4;
		int var5;
		int var6;
		String pattern;
		if (!ObjectUtils.isEmpty(this.excludePatterns)) {
			var4 = this.excludePatterns;
			var5 = var4.length;

			for(var6 = 0; var6 < var5; ++var6) {
				pattern = var4[var6];
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return false;
				}
			}
		}

		if (ObjectUtils.isEmpty(this.includePatterns)) {
			return true;
		} else {
			var4 = this.includePatterns;
			var5 = var4.length;

			for(var6 = 0; var6 < var5; ++var6) {
				pattern = var4[var6];
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean preHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) throws Exception {
		return this.interceptor.preHandle(requestParser, responseWrapper, route, targetMethod);
	}

	public void postHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) throws Exception {
		this.interceptor.postHandle(requestParser, responseWrapper, route, targetMethod);
	}
}
