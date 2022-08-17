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

import fun.asgc.neutrino.core.util.CollectionUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
public class ByteCodeClassLoader extends ClassLoader {
	private Map<String, byte[]> byteCodeMap = new ConcurrentHashMap<>();
	static {
		registerAsParallelCapable();
	}

	public ByteCodeClassLoader() {
		super(getParentClassLoader());
	}

	protected static ClassLoader getParentClassLoader() {
		ClassLoader ret = Thread.currentThread().getContextClassLoader();
		return ret != null ? ret : ByteCodeClassLoader.class.getClassLoader();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] bytes = byteCodeMap.get(name);
		if (null != bytes) {
			Class<?> ret = defineClass(name, bytes, 0, bytes.length);
			byteCodeMap.remove(name);
			return ret;
		}
		return super.findClass(name);
	}

	public void addByteCode(Map<String, byte[]> byteCodeMap) {
		if (!CollectionUtil.isEmpty(byteCodeMap)) {
			for (Map.Entry<String, byte[]> e : byteCodeMap.entrySet()) {
				this.byteCodeMap.putIfAbsent(e.getKey(), e.getValue());
			}
		}
	}
}
