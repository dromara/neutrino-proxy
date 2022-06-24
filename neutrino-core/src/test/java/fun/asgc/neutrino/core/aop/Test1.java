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

import org.junit.Test;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class Test1 {

	@Test
	public void dogCall() {
		Dog dog = Aop.get(Dog.class);
		dog.call();
	}

	@Test
	public void dogSay() {
		Dog dog = Aop.get(Dog.class);
		System.out.println(dog.say("hello"));
	}

	@Test
	public void catClimb() {
		Cat cat = Aop.get(Cat.class);
		cat.climb();
	}

	@Test
	public void catCalc() {
		Cat cat = Aop.get(Cat.class);
		System.out.println(cat.calc(10, 6));
	}

}
