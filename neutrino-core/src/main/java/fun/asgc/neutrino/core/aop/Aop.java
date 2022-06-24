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
import fun.asgc.neutrino.core.aop.proxy.ProxyFactory;
import fun.asgc.neutrino.core.aop.proxy.ProxyStrategy;
import fun.asgc.neutrino.core.util.Assert;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class Aop {
	private static final ProxyStrategy proxyStrategy = ProxyStrategy.SUB_CLASS_PROXY;
	private static final ProxyFactory proxyFactory = Proxy.getProxyFactory(proxyStrategy);

	public static <T> T get(Class<T> clazz) {
		Assert.notNull(proxyFactory, "代理工厂初始化异常!");
		return proxyFactory.get(clazz);
	}
}
