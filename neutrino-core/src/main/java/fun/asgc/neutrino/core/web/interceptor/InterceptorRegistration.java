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

import fun.asgc.neutrino.core.base.Ordered;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.web.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/27
 */
public class InterceptorRegistration {
	private final HandlerInterceptor interceptor;
	private final List<String> includePatterns = new ArrayList<>();
	private final List<String> excludePatterns = new ArrayList<>();
	private PathMatcher pathMatcher;
	private int order = 0;

	public InterceptorRegistration(HandlerInterceptor interceptor) {
		Assert.notNull(interceptor, "Interceptor is required");
		this.interceptor = interceptor;
	}

	public InterceptorRegistration addPathPatterns(String... patterns) {
		return this.addPathPatterns(Arrays.asList(patterns));
	}

	public InterceptorRegistration addPathPatterns(List<String> patterns) {
		this.includePatterns.addAll(patterns);
		return this;
	}

	public InterceptorRegistration excludePathPatterns(String... patterns) {
		return this.excludePathPatterns(Arrays.asList(patterns));
	}

	public InterceptorRegistration excludePathPatterns(List<String> patterns) {
		this.excludePatterns.addAll(patterns);
		return this;
	}

	public InterceptorRegistration pathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
		return this;
	}

	public InterceptorRegistration order(int order) {
		this.order = order;
		return this;
	}

	protected int getOrder() {
		return this.order;
	}

	public Object getInterceptor() {
		if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) {
			return this.interceptor;
		} else {
			String[] include = StringUtil.toStringArray(this.includePatterns);
			String[] exclude = StringUtil.toStringArray(this.excludePatterns);
			MappedInterceptor mappedInterceptor = new MappedInterceptor(include, exclude, this.interceptor);
			if (this.pathMatcher != null) {
				mappedInterceptor.setPathMatcher(this.pathMatcher);
			}

			return mappedInterceptor;
		}
	}

	Ordered toOrdered() {
		return new Ordered() {
			@Override
			public int getOrder() {
				return order;
			}
		};
	}
}
