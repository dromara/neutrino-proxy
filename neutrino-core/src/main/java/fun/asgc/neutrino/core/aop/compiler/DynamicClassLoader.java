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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/25
 */
public class DynamicClassLoader extends ClassLoader {
	private final Map<String, MemoryByteCode> byteCodes = new HashMap<>();

	public DynamicClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}

	public void registerCompiledSource(MemoryByteCode byteCode) {
		byteCodes.put(byteCode.getClassName(), byteCode);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		MemoryByteCode byteCode = byteCodes.get(name);
		if (null == byteCode) {
			return super.findClass(name);
		}

		return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
	}

	public Map<String, Class<?>> getClasses() throws ClassNotFoundException {
		Map<String, Class<?>> classes = new HashMap<>();
		for (MemoryByteCode byteCode : byteCodes.values()) {
			classes.put(byteCode.getClassName(), findClass(byteCode.getClassName()));
		}
		return classes;
	}

	public Map<String, byte[]> getByteCodes() {
		Map<String, byte[]> result = new HashMap<>(byteCodes.size());
		for (Map.Entry<String, MemoryByteCode> entry : byteCodes.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getByteCode());
		}
		return result;
	}
}
