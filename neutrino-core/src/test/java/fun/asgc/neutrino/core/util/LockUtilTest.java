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

import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/20
 */
public class LockUtilTest {
	private static volatile A a;

	/**
	 * 没有双重校验锁，并发执行容易产生多个实例
	 */
	@Test
	public void test1() {
		for (int i = 0; i < 10; i++) {
			ThreadUtil.run(() -> {
				if (a == null) {
					a = new A();
				}
			});
		}
		SystemUtil.waitProcessDestroy().sync();
	}

	/**
	 * 使用双重校验锁，并发执行，只产生一个实例
	 */
	@Test
	public void test2() {
		for (int i = 0; i < 10; i++) {
			ThreadUtil.run(() -> {
				LockUtil.doubleCheckProcess(() -> a == null,
					LockUtilTest.class,
					() -> a = new A());
			});
		}
		SystemUtil.waitProcessDestroy().sync();
	}

	@Data
	public static class A {
		public A() {
			System.out.println("new instance.");
		}
	}
}
