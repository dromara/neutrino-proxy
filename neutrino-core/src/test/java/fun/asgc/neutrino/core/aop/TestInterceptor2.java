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
package fun.asgc.neutrino.core.aop;

import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
@Slf4j
public class TestInterceptor2 implements Interceptor {

	public TestInterceptor2() {
		System.out.println("拦截器2实例化");
	}

	@Override
	public void intercept(Invocation inv) {
		try {
			log.info("拦截器2 class:{} method:{} args:{} before", inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
			inv.invoke();
			log.info("拦截器2 class:{} method:{} args:{} after", inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
		} catch (Exception e) {
			log.info("拦截器2 class:{} method:{} args:{} error", inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
			e.printStackTrace();
		}
	}
}
