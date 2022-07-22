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

import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

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
	}

	public static HttpRequestParser create(FullHttpRequest request) {
		return new HttpRequestParser(request);
	}

	public String getUrl() {
		return url;
	}

	public String getQueryString() {
		return queryString;
	}

	public Map<String, String> getQueryParamMap() {
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == queryParamMap,
			this,
			() -> {
				queryParamMap = new HashMap<>();
				if (StringUtil.isEmpty(queryString)) {
					return;
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
						queryParamMap.put(key, val);
					}
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
}
