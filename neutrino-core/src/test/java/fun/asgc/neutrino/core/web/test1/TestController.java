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

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.web.annotation.GetMapping;
import fun.asgc.neutrino.core.web.annotation.RequestMapping;
import fun.asgc.neutrino.core.web.annotation.RequestParam;
import fun.asgc.neutrino.core.web.annotation.RestController;
import fun.asgc.neutrino.core.web.param.HttpContextHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/23
 */
@Slf4j
@RestController
@RequestMapping("/test1")
public class TestController {
	@Autowired
	private TestService testService;

	@GetMapping("hello")
	public String hello() {
		System.out.println("拿到参数 a = " + HttpContextHolder.getHttpRequestParser().getParameterForInteger("a"));
		return testService.hello();
	}

	@GetMapping("add")
	public Integer add(@RequestParam("x") int x, @RequestParam("y") int y) {
		log.info("另一种取参方式 msg:{}", HttpContextHolder.getHttpRequestParser().getParameterForString("msg"));
		return x + y;
	}

	@GetMapping("hello2")
	public void hello2(ChannelHandlerContext context) {
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer("aoshiguchen".getBytes()));
		fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		context.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
	}

	@GetMapping("hello3")
	public void hello3(@RequestParam("a") String a, @RequestParam("b") String b, @RequestParam(value = "c", required = false) String c) {
		log.info("a:{} b:{} c:{}", a, b, c);
	}
}
