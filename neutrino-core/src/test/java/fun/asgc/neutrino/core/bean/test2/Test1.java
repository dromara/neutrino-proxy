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
package fun.asgc.neutrino.core.bean.test2;

import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/5
 */
public class Test1 {

	@Test
	public void test1() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		PropertyDescriptor pd1= new PropertyDescriptor("name", Person.class);
		PropertyDescriptor pd2= new PropertyDescriptor("age", Person.class, "getAge", "setAge");

		Person person = new Person();
		person.setName("张三");

		pd1.getWriteMethod().invoke(person, "李四");

		System.out.println(pd1.getReadMethod().invoke(person));

		pd2.getWriteMethod().invoke(person, 20);
		System.out.println(pd2.getReadMethod().invoke(person));
	}

	public static class Person {
		private String name;
		private int age;

		public String isName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}
}
