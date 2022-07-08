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

import fun.asgc.neutrino.core.util.ReflectUtil;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/7
 */
public class Test5 {

	@Test
	public void test1() throws Exception {
		Aop.intercept(Panda.class, TestInterceptor.class);
		Panda panda = Aop.get(Panda.class);
		panda.eat();
		panda.say("hello");
	}

	@Test
	public void test2() throws Exception {
		// 只拦截一个方法
		Method method = ReflectUtil.getMethods(Panda.class).stream().filter(m -> m.getName().equals("say")).findFirst().get();
		Aop.intercept(method, TestInterceptor.class);

		Panda panda = Aop.get(Panda.class);
		panda.eat();

		panda.say("hello");
	}
}
