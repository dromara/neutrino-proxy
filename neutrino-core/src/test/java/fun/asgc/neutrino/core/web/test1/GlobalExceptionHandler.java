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
package fun.asgc.neutrino.core.web.test1;

import fun.asgc.neutrino.core.web.context.HttpRequestParser;
import fun.asgc.neutrino.core.web.interceptor.RestControllerExceptionHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/29
 */
public class GlobalExceptionHandler implements RestControllerExceptionHandler {

	@Override
	public Object handle(ChannelHandlerContext context, HttpRequestParser requestParser, Throwable e) {
		if (e instanceof UserNotLoginException) {
			return new JsonResult<>()
				.setCode(101)
				.setMsg("账号未登录")
				.setStack(ExceptionUtils.getStackTrace(e));
		}
		return new JsonResult<>()
			.setCode(1)
			.setMsg("系统异常")
			.setStack(ExceptionUtils.getStackTrace(e));
	}

}
