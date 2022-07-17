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

import fun.asgc.neutrino.core.base.Identity;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.web.HttpMethod;

/**
 * http路由唯一标识
 * @author: aoshiguchen
 * @date: 2022/7/16
 */
public class HttpRouteIdentity implements Identity {

	/**
	 * http方法
	 */
	private HttpMethod method;
	/**
	 * url
	 */
	private String url;
	/**
	 * hashCode
	 */
	private int identityHashCode;

	@Override
	public boolean isOnly() {
		return true;
	}

	public HttpRouteIdentity(HttpMethod method, String url) {
		Assert.notNull(method, "http方法不能为空！");
		Assert.notEmpty(url, "url不能为空!");
		this.method = method;
		this.url = url;
		this.identityHashCode = System.identityHashCode(method);
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof HttpRouteIdentity)) {
			return false;
		}
		HttpRouteIdentity httpRouteIdentity = (HttpRouteIdentity)obj;
		return this.method.equals(httpRouteIdentity.getMethod()) && this.url.equals(httpRouteIdentity.getUrl());
	}

	@Override
	public int hashCode() {
		return identityHashCode;
	}
}
