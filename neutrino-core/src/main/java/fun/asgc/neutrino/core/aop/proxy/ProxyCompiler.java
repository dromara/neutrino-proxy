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

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class ProxyCompiler {
	protected volatile List<String> options = null;

	protected List<String> getOptions() {
		if (options != null) {
			return options;
		}

		synchronized (this) {
			if (options != null) {
				return options;
			}

			List<String> ret = new ArrayList<>();
			ret.add("-target");
			ret.add("1.8");

			String cp = getClassPath();
			if (cp != null && cp.trim().length() != 0) {
				ret.add("-classpath");
				ret.add(cp);
			}

			options = ret;
			return options;
		}
	}

	/**
	 * 兼容 tomcat 丢失 class path，否则无法编译
	 */
	protected String getClassPath() {
		URLClassLoader classLoader = getURLClassLoader();
		if (classLoader == null) {
			return null;
		}

		int index = 0;
		boolean isWindows = isWindows();
		StringBuilder ret = new StringBuilder();
		for (URL url : classLoader.getURLs()) {
			if (index++ > 0) {
				ret.append(File.pathSeparator);
			}

			String path = url.getFile();

			// 如果是 windows 系统，去除前缀字符 '/'
			if (isWindows && path.startsWith("/")) {
				path = path.substring(1);
			}

			// 去除后缀字符 '/'
			if (path.length() > 1 && (path.endsWith("/") || path.endsWith(File.separator))) {
				path = path.substring(0, path.length() - 1);
			}

			ret.append(path);
		}

		return ret.toString();
	}

	protected boolean isWindows() {
		String osName = System.getProperty("os.name", "unknown");
		return osName.toLowerCase().indexOf("windows") != -1;
	}

	protected URLClassLoader getURLClassLoader() {
		ClassLoader ret = Thread.currentThread().getContextClassLoader();
		if (ret == null) {
			ret = ProxyCompiler.class.getClassLoader();
		}
		return (ret instanceof URLClassLoader) ? (URLClassLoader)ret : null;
	}

	public void compile(ProxyClass proxyClass) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new RuntimeException("Can not get javax.tools.JavaCompiler, check whether \"tools.jar\" is in the environment variable CLASSPATH \n" +
				"Visit https://jfinal.com/doc/4-8 for details \n");
		}

		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		try (MyJavaFileManager javaFileManager = new MyJavaFileManager(compiler.getStandardFileManager(collector, null, null))) {

			MyJavaFileObject javaFileObject = new MyJavaFileObject(proxyClass.getName(), proxyClass.getSourceCode());
			Boolean result = compiler.getTask(null, javaFileManager, collector, getOptions(), null, Arrays.asList(javaFileObject)).call();
			outputCompileError(result, collector);

			Map<String, byte[]> ret = new HashMap<>();
			for (Map.Entry<String, MyJavaFileObject> e : javaFileManager.fileObjects.entrySet()) {
				ret.put(e.getKey(), e.getValue().getByteCode());
			}

			proxyClass.setByteCode(ret);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void outputCompileError(Boolean result, DiagnosticCollector<JavaFileObject> collector) {
		if (! result) {
			// collector.getDiagnostics().forEach(item -> log.error(item.toString()));
			collector.getDiagnostics().forEach(item -> System.out.println(item.toString()));
		}
	}

	public ProxyCompiler setCompileOptions(List<String> options) {
		Objects.requireNonNull(options, "options can not be null");
		this.options = options;
		return this;
	}

	public ProxyCompiler addCompileOption(String option) {
		Objects.requireNonNull(option, "option can not be null");
		options.add(option);
		return this;
	}

	public static class MyJavaFileObject extends SimpleJavaFileObject {

		private String source;
		private ByteArrayOutputStream outPutStream;

		public MyJavaFileObject(String name, String source) {
			super(URI.create("String:///" + name + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
			this.source = source;
		}

		public MyJavaFileObject(String name, JavaFileObject.Kind kind) {
			super(URI.create("String:///" + name + kind.extension), kind);
			source = null;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			if (source == null) {
				throw new IllegalStateException("source field can not be null");
			}
			return source;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			outPutStream = new ByteArrayOutputStream();
			return outPutStream;
		}

		public byte[] getByteCode() {
			return outPutStream.toByteArray();
		}
	}

	public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

		public Map<String, MyJavaFileObject> fileObjects = new HashMap<>();

		public MyJavaFileManager(JavaFileManager fileManager) {
			super(fileManager);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
			MyJavaFileObject javaFileObject = new MyJavaFileObject(qualifiedClassName, kind);
			fileObjects.put(qualifiedClassName, javaFileObject);
			return javaFileObject;
		}

		// 是否在编译时依赖另一个类的情况下用到本方法 ?
		@Override
		public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException {
			JavaFileObject javaFileObject = fileObjects.get(className);
			if (javaFileObject == null) {
				javaFileObject = super.getJavaFileForInput(location, className, kind);
			}
			return javaFileObject;
		}
	}
}
