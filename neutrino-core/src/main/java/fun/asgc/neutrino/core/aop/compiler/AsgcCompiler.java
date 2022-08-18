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

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.base.GlobalConfig;
import fun.asgc.neutrino.core.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import javax.tools.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
@Slf4j
@SuppressWarnings("all")
public class AsgcCompiler {
	private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private DiagnosticCollector<JavaFileObject> collector;
	private List<String> classpathList;
	private boolean isSaveSourceCodeFile;
	private boolean isSaveClassFile;
	private String generatorCodeSavePath;
	private ByteCodeClassLoader classLoader;

	public AsgcCompiler() {
		if (null == compiler) {
			throw new RuntimeException("Can not get javax.tools.JavaCompiler, check whether \"tools.jar\" is in the environment variable CLASSPATH \nVisit https://jfinal.com/doc/4-8 for details \n");
		}
		this.collector = new DiagnosticCollector<>();
		this.classpathList = Lists.newArrayList();
		this.isSaveClassFile = false;
		this.generatorCodeSavePath = GlobalConfig.getGeneratorCodeSavePath();
		this.classLoader = new ByteCodeClassLoader();
	}

	public void addClasspath(String classpath) {
		this.classpathList.add(classpath);
	}

	public void setSaveClassFile(boolean saveClassFile) {
		this.isSaveClassFile = saveClassFile;
	}

	public void setSaveSourceCodeFile(boolean saveSourceCodeFile) {
		isSaveSourceCodeFile = saveSourceCodeFile;
	}

	public void setGeneratorCodeSavePath(String generatorCodeSavePath) {
		this.generatorCodeSavePath = generatorCodeSavePath;
	}

	private List<String> getOptions() {
		List<String> options = Lists.newArrayList();
		options.add("-source");
		options.add("1.8");
		options.add("-target");
		options.add("1.8");
		if (!CollectionUtil.isEmpty(classpathList)) {
			options.add("-classpath");
			options.add(classpathList.stream().collect(Collectors.joining(File.pathSeparator)));
		}
		return options;
	}

	/**
	 * 编译代码
	 * @param className 类名
	 * @param sourceCode 源代码
	 */
	public Map<String,byte[]> compile(String className, String sourceCode) {
		DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(compiler.getStandardFileManager(collector, null, null));
		CharSequenceJavaFileObject javaFileObject = new CharSequenceJavaFileObject(className, sourceCode);
		Boolean result = compiler.getTask(null, javaFileManager, collector, getOptions(), null, Arrays.asList(javaFileObject)).call();
		if (!result) {
			collector.getDiagnostics().forEach(item -> log.error(item.toString()));
		}

		Map<String, byte[]> ret = new HashMap<>();
		for (Map.Entry<String, CharSequenceJavaFileObject> e : javaFileManager.fileObjects.entrySet()) {
			ret.put(e.getKey(), e.getValue().getByteCode());
		}
		return ret;
	}

	/**
	 * 编译并加载类
	 * @param pkg
	 * @param className
	 * @param sourceCode
	 * @param <T>
	 * @return
	 */
	public <T> Class<T> compileAndLoadClass(String pkg, String className, String sourceCode) throws ClassNotFoundException {
		Map<String,byte[]> byteCodeMap = compile(className, sourceCode);
		classLoader.addByteCode(byteCodeMap);
		return (Class<T>)classLoader.loadClass(pkg + "." + className);
	}
}
