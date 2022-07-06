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

import fun.asgc.neutrino.core.aop.proxy.Proxy;
import fun.asgc.neutrino.core.util.ReflectUtil;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class Test3 {

	@Test
	public void test1() throws Exception {
//		Animal animal = Aop.get(Animal.class);
		Animal animal = Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY).get(Animal.class);
		System.out.println(animal);
		System.out.println(animal.say("aa"));
	}

	@Test
	public void test2() throws Exception {
//		Mammal mammal = Aop.get(Mammal.class);
		Mammal mammal = Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY).get(Mammal.class);
		mammal.crawl();

		System.out.println(ReflectUtil.getInterfaceAll(Mammal.class));
	}

	@Test
	public void test3() throws Exception {
		Bear bear = Aop.get(Bear.class);
		bear.up();
	}

	@Test
	public void test4() throws InvocationTargetException, IllegalAccessException {
		Method hello1 = ReflectUtil.getMethods(A.class).stream().filter(m -> m.getName().equals("hello1")).findFirst().get();
		Method hello11 = ReflectUtil.getMethods(B.class).stream().filter(m -> m.getName().equals("hello1")).findFirst().get();
		A a = new A();
		B b = new B();
//		hello1.invoke(a);
//		hello1.invoke(b);
		hello11.invoke(a); // 异常
		hello11.invoke(b);
	}

	public static class A {
		public void hello1() {
			System.out.println("hello1");
		}
		public void hello2() {
			System.out.println("hello2");
		}
	}

	public static class B extends A {
		@Override
		public void hello1() {
			super.hello1();
			System.out.println("----");
		}
	}
}
