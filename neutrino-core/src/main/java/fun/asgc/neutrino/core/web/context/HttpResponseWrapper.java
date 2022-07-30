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

import fun.asgc.neutrino.core.util.Assert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/30
 */
public class HttpResponseWrapper {
	private HttpResponseStatus status;
	private HttpHeaders httpHeaders;
	private ByteBuf content;


	private HttpResponseWrapper() {
		this.status = HttpResponseStatus.OK;
		this.httpHeaders = new DefaultHttpHeaders();
		this.content = Unpooled.EMPTY_BUFFER;
	}

	public HttpHeaders headers() {
		return httpHeaders;
	}

	public void setContent(ByteBuf content) {
		this.content = content;
	}

	public void setStatus(HttpResponseStatus status) {
		Assert.notNull(status, "http响应状态码不能为空!");
		this.status = status;
	}

	public void writeAndFlush() {
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
		fullHttpResponse.headers().add(httpHeaders);
		HttpContextHolder.getChannelHandlerContext().writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
	}

	public static HttpResponseWrapper create() {
		return new HttpResponseWrapper();
	}
}
