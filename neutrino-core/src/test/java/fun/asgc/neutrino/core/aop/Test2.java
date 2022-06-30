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

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.aop.interceptor.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
@Slf4j
public class Test2 {

	{
		// 注册全局拦截器
		InterceptorFactory.registerGlobalInterceptor(GlobalInterceptor.class);
		// 注册过滤器
		InnerGlobalInterceptor.registerFilter(new Filter() {
			@Override
			public boolean filtration(Class<?> targetClass, Method targetMethod, Object[] args) {
				if (targetClass == Panda.class && targetMethod.getName().equals("say")) {
					return true;
				}
				return false;
			}
		});
		// 注册结果处理器
		InnerGlobalInterceptor.registerResultAdvice(new ResultAdvice() {
			@Override
			public Object advice(Class<?> targetClass, Method targetMethod, Object result) {
				if (targetClass == Panda.class && targetMethod.getName().equals("request")) {
					JSONObject data = new JSONObject();
					data.put("code", 0);
					data.put("data", result);
					return data.toJSONString();
				}
				return result;
			}
		});
		// 注册异常处理器
		InnerGlobalInterceptor.registerExceptionHandler(new ExceptionHandler() {
			@Override
			public boolean support(Exception e) {
				return e instanceof ArithmeticException;
			}

			@Override
			public Object handle(Exception e) {
				log.info("除数不能为0!");
				return null;
			}
		});
	}

	@Test
	public void eat() {
		Panda panda = Aop.get(Panda.class);
		panda.eat();
	}

	@Test
	public void play() {
		Panda panda = Aop.get(Panda.class);
		panda.play("滑板");
	}

	@Test
	public void division() {
		Panda panda = Aop.get(Panda.class);
		System.out.println(panda.division(10, 5));
		panda.division(10, 0);
	}

	@Test
	public void say() {
		Panda panda = Aop.get(Panda.class);
		panda.say("hello");
	}

	@Test
	public void request() {
		Panda panda = Aop.get(Panda.class);
		panda.request("xxx", "yyy");
	}

	@Test
	public void up() {
		Panda panda = Aop.get(Panda.class);
		try {
			panda.up();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
