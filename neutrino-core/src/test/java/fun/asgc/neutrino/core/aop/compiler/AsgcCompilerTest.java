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
package fun.asgc.neutrino.core.aop.compiler;

import fun.asgc.neutrino.core.aop.proxy.ProxyClass;
import fun.asgc.neutrino.core.aop.proxy.ProxyClassLoader;
import fun.asgc.neutrino.core.aop.proxy.ProxyCompiler;
import fun.asgc.neutrino.core.util.ReflectUtil;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
public class AsgcCompilerTest {

	@Test
	public void test1() throws IllegalAccessException, InstantiationException, InvocationTargetException {
		AsgcCompiler compiler = new AsgcCompiler();
		String code = "package a.b;\n" +
			"public class Hello {\n" +
			"\tpublic void hello() {\n" +
			"\t\tSystem.out.println(\"hello\");\n" +
			"\t}\n" +
			"}\n";

		ProxyClass proxyClass = new ProxyClass();
		proxyClass.setPkg("a.b");
		proxyClass.setName("Hello");
		proxyClass.setSourceCode(code);
		ProxyCompiler proxyCompiler = new ProxyCompiler();
		proxyCompiler.compile(proxyClass);
		ProxyClassLoader proxyClassLoader = new ProxyClassLoader();
		Class clazz = proxyClassLoader.loadProxyClass(proxyClass);
		Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("hello")).findFirst().get();
		Object instance = clazz.newInstance();
		method.invoke(instance);
	}

	@Test
	public void test2() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		AsgcCompiler compiler = new AsgcCompiler();
		String code = "package a.b;\n" +
			"public class Hello {\n" +
			"\tpublic void hello() {\n" +
			"\t\tSystem.out.println(\"hello\");\n" +
			"\t}\n" +
			"}\n";
		Class clazz = compiler.compileAndLoadClass("a.b","Hello", code);
		Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("hello")).findFirst().get();
		Object instance = clazz.newInstance();
		method.invoke(instance);
	}

}
