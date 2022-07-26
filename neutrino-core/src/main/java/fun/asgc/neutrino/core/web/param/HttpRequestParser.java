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
package fun.asgc.neutrino.core.web.param;

import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Http请求解析器
 * @author: aoshiguchen
 * @date: 2022/7/22
 */
public class HttpRequestParser {
	private FullHttpRequest request;
	private String url;
	private String queryString;
	private Map<String, String> queryParamMap = null;
	private String routePath;

	private HttpRequestParser(FullHttpRequest request) {
		this.request = request;
		this.url = request.uri();
		this.queryString = "";

		int index = url.indexOf("?");
		if (index >= 0) {
			if (index < url.length() - 1) {
				this.queryString = url.substring(index + 1);
			}
			url = getUrl().substring(0, index);
		}
		this.routePath = getRoutePath(url);
	}

	public static HttpRequestParser create(FullHttpRequest request) {
		return new HttpRequestParser(request);
	}

	public String getUrl() {
		return url;
	}

	public String getRoutePath() {
		return routePath;
	}

	public String getQueryString() {
		return queryString;
	}

	public Map<String, String> getQueryParamMap() {
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == queryParamMap,
			this,
			() -> {
				queryParamMap = queryStringToMap(queryString);
				if ("application/x-www-form-urlencoded".equals(getContentType())) {
					queryParamMap.putAll(queryStringToMap(getContentAsString()));
				}
			},
			() -> queryParamMap
		);
	}

	public HttpMethod getMethod() {
		return request.method();
	}

	public ByteBuf getContent() {
		return request.content();
	}

	public String getContentAsString() {
		return getContent().toString(CharsetUtil.UTF_8);
	}

	public String getParameter(String key) {
		return getQueryParamMap().get(key);
	}

	public Byte getParameterForByte(String key) {
		return getParameterAndConvert(key, Byte::valueOf);
	}

	public Short getParameterForShort(String key) {
		return getParameterAndConvert(key, Short::valueOf);
	}

	public Integer getParameterForInteger(String key) {
		return getParameterAndConvert(key, Integer::valueOf);
	}

	public Long getParameterForLong(String key) {
		return getParameterAndConvert(key, Long::valueOf);
	}

	public Float getParameterForFloat(String key) {
		return getParameterAndConvert(key, Float::valueOf);
	}

	public Double getParameterForDouble(String key) {
		return getParameterAndConvert(key, Double::valueOf);
	}

	public Boolean getParameterForBoolean(String key) {
		return getParameterAndConvert(key, Boolean::valueOf);
	}

	public String getParameterForString(String key) {
		return getParameterAndConvert(key, Function.identity());
	}

	public <T> T getParameterAndConvert(String key, Function<String,T> convert) {
		Assert.notEmpty(key, "key不能为空！");
		Assert.notNull(convert, "convert不能为空！");
		String val = getParameter(key);
		if (StringUtil.notEmpty(val)) {
			return convert.apply(val);
		}
		return null;
	}

	public String getHeaderValue(String name) {
		return request.headers().get(name);
	}

	public String getContentType() {
		return request.headers().get("Content-Type");
	}

	private Map<String, String> queryStringToMap(String queryString) {
		Map<String, String> result = new HashMap<>();
		if (StringUtil.isEmpty(queryString)) {
			return result;
		}
		String[] params = queryString.split("&");
		for (String param : params) {
			int index2 = param.indexOf("=");
			String key = "";
			String val = "";
			if (index2 > 0) {
				key = param.substring(0, index2);
				if (index2 < param.length() - 1) {
					val = param.substring(index2 + 1);
				}
				result.put(key, val);
			}
		}
		return result;
	}

	private String getRoutePath(String url) {
		String httpContextPath = WebContextHolder.getHttpContextPath();
		if (StringUtil.isEmpty(httpContextPath)) {
			return url;
		}
		String res = url.substring(httpContextPath.length());
		if (StringUtil.isEmpty(res)) {
			return "/";
		}
		return res;
	}
}
