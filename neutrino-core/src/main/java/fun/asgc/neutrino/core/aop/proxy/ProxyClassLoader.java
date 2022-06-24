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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理类加载器
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class ProxyClassLoader extends ClassLoader {

	protected Map<String, byte[]> byteCodeMap = new ConcurrentHashMap<>();

	static {
		registerAsParallelCapable();
	}

	public ProxyClassLoader() {
		super(getParentClassLoader());
	}

	protected static ClassLoader getParentClassLoader() {
		ClassLoader ret = Thread.currentThread().getContextClassLoader();
		return ret != null ? ret : ProxyClassLoader.class.getClassLoader();
	}

	public Class<?> loadProxyClass(ProxyClass proxyClass) {
		for (Map.Entry<String, byte[]> e : proxyClass.getByteCode().entrySet()) {
			byteCodeMap.putIfAbsent(e.getKey(), e.getValue());
		}

		try {
			return loadClass(proxyClass.getPkg() + "." + proxyClass.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] bytes = byteCodeMap.get(name);
		if (bytes != null) {
			Class<?> ret = defineClass(name, bytes, 0, bytes.length);
			byteCodeMap.remove(name);
			return ret;
		}

		return super.findClass(name);
	}
}
