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

import fun.asgc.neutrino.core.util.ClassUtil;
import fun.asgc.neutrino.core.util.CollectionUtil;

import java.net.URL;
import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/25
 */
public class DynamicClassLoader extends ClassLoader {
	private final Map<String, MemoryByteCode> byteCodes = new HashMap<>();
	private AsgcCompiler compiler;

	public DynamicClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}

	public DynamicClassLoader(AsgcCompiler compiler, ClassLoader classLoader) {
		super(classLoader);
		this.compiler = compiler;
	}

	public void registerCompiledSource(MemoryByteCode byteCode) {
		byteCodes.put(byteCode.getClassName(), byteCode);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		MemoryByteCode byteCode = byteCodes.get(name);
		if (null != byteCode) {
			return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
		}
		Class<?> ret = doFindClass(name);
		if (null != ret) {
			return ret;
		}
		return super.findClass(name);
	}

	private Class<?> doFindClass(String name) throws ClassNotFoundException {
		if (null == compiler) {
			return null;
		}
		List<String> classpathList = compiler.getClasspathList();
		if (CollectionUtil.isEmpty(classpathList)) {
			return null;
		}
		String packageName = "";
		if (name.lastIndexOf(".") != -1) {
			packageName = name.substring(0, name.lastIndexOf("."));
		}
		for (String path : classpathList) {
			try {
				URL url = new URL("file:" + path);
				if (path.endsWith(".jar")) {
					url = new URL("jar:file:" + path + "!/");
				}
				Set<Class<?>> classSet = ClassUtil.scan(packageName, url);
				if (CollectionUtil.isEmpty(classSet)) {
					continue;
				}
				Optional<Class<?>> classOptional = classSet.stream().filter(c -> c.getName().equals(name)).findFirst();
				if (classOptional.isPresent()) {
					return classOptional.get();
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
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
