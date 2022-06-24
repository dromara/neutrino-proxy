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
package fun.asgc.neutrino.core.aop.proxy;

import lombok.Data;

import java.util.Map;

/**
 *
 * @author: 代理类
 * @date: 2022/6/24
 */
@Data
public class ProxyClass {
	/**
	 * 被代理的目标
	 */
	private Class<?> target;
	/**
	 * 包名
	 */
	private String pkg;
	/**
	 * 类名
 	 */
	private String name;
	/**
	 * 源代码
	 */
	private String sourceCode;
	/**
	 * 字节码
	 */
	private Map<String, byte[]> byteCode;
	/**
	 * 字节码被加载后的代理类
	 */
	private Class<?> clazz;

	public ProxyClass(Class<?> target) {
		this.target = target;
		this.pkg = target.getPackage().getName();
	}
}
