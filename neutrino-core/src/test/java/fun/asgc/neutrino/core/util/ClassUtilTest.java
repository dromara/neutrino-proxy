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
package fun.asgc.neutrino.core.util;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/4
 */
public class ClassUtilTest {

	@Test
	public void hasNoArgsConstructor() {
//		System.out.println(ClassUtil.hasNoArgsConstructor(ClassUtilTest.class));
		Pattern pattern = Pattern.compile("\\$\\{[\\s\\p{Zs}]*(.*):(.*)[\\s\\p{Zs}]*\\}");
		String s = "${  a:bb11  }";
		Matcher m = pattern.matcher(s);
		if (m.find()) {
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}

	}

	@Test
	public void test1() throws InvocationTargetException, IllegalAccessException {
		Method method = ReflectUtil.getMethods(Student.class).stream().filter(m -> m.getName().equals("say")).findFirst().get();
		Student student = new Student();
		method.invoke(student, new Object[]{"1", "2"});
	}

	@Test
	public void test2() throws InvocationTargetException, IllegalAccessException {
		Method method = ReflectUtil.getMethods(Student.class).stream().filter(m -> m.getName().equals("hello")).findFirst().get();
		Student student = new Student();
		method.invoke(student, new Object[0]);
	}

	public static class Student {
		public void say(String msg1, String msg2) {
			System.out.println("msg1:" + msg1 + " msg2:" + msg2);
		}
		public void hello() {
			System.out.println("hello");
		}
	}

	@Test
	public void scan() throws Exception {
		String path = "jar:file:/Users/yangwen/my/tmp/java/neutrino-proxy-server-1.0-SNAPSHOT-jar-with-dependencies.jar!/";
		URL url = new URL(path);
		Set<Class<?>> c = ClassUtil.scan("fun.asgc.neutrino.proxy.server", url);
		System.out.println(c);
	}
}
