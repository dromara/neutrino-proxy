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

import fun.asgc.neutrino.core.util.ArrayUtil;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 动态类加载器
 * @author: aoshiguchen
 * @date: 2022/8/25
 */
public class DynamicClassLoader extends ClassLoader {
	private final Map<String, MemoryByteCode> byteCodes = new HashMap<>();
	private AsgcCompiler compiler;
	private Map<String, Class<?>> classMap = new HashMap<>();

	public DynamicClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}

	public DynamicClassLoader(ClassLoader classLoader, AsgcCompiler compiler) {
		super(classLoader);
		this.compiler = compiler;
	}

	public void registerCompiledSource(MemoryByteCode byteCode) {
		byteCodes.put(byteCode.getClassName(), byteCode);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (classMap.containsKey(name)) {
			return classMap.get(name);
		}
		MemoryByteCode byteCode = byteCodes.get(name);
		if (null != byteCode) {
			return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
		}
		if (null != this.compiler) {
			Class<?> ret = doFindClass(name, compiler.getClasspathList());
			if (null != ret) {
				return ret;
			}
		}
		return super.findClass(name);
	}

	private Class<?> doFindClass(String name, List<String> classpathList) throws ClassNotFoundException {
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
				Set<Class<?>> classSet = scan(packageName, url);
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

	public Set<Class<?>> scan(String packageName, URL url) throws IOException, ClassNotFoundException, URISyntaxException {
		Set<Class<?>> result = new HashSet<>();
		if (null == url) {
			return result;
		}
		String packagePath = packageName.replace(".", "/");
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
		String protocol = url.getProtocol();
		if ("jar".equals(protocol)) {
			JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
			JarFile jarFile = jarURLConnection.getJarFile();
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				int index = name.indexOf(packagePath);
				if (index != -1 && name.endsWith(".class")) {
					String replace = name.substring(index, name.length() - 6).replace("/", ".");
					Class clazz = urlClassLoader.loadClass(replace);
					result.add(clazz);
				}
			}
		} else if ("file".endsWith(protocol)) {
			String path = url.getPath();
			String targetPath = path + "/" + packagePath;
			addClasses(targetPath, result, packageName);
		}
		return result;
	}

	private synchronized void addClasses(String path, Set<Class<?>> classes, String packageName) throws ClassNotFoundException, URISyntaxException {
		File[] files = new File(path).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
		if (ArrayUtil.isEmpty(files)) {
			return;
		}

		for (File file : files) {
			String fileName = file.getName();
			if (file.isFile()) {
				String className = fileName.substring(0, fileName.lastIndexOf("."));
				String fullClassName = packageName + "." + className;
				Class clazz = null;
				try {
					clazz = Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
				} catch (ClassNotFoundException e) {
					if (this.classMap.containsKey(fullClassName)) {
						clazz = this.classMap.get(fileName);
					} else {
						byte[] byteCode = FileUtil.readBytes(file);
						clazz = super.defineClass(fullClassName, byteCode, 0, byteCode.length);
						this.classMap.put(fullClassName, clazz);
					}
				}
				if (null != clazz) {
					classes.add(clazz);
				}
			} else {
				String subPackagePath = path + "/" + fileName;
				String subPackageName = packageName + "." + fileName;
				addClasses(subPackagePath, classes, subPackageName);
			}
		}
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
