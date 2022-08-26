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
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
@Slf4j
@SuppressWarnings("all")
public class AsgcCompiler {
	private final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
	private final DiagnosticCollector<JavaFileObject> collector;
	private final StandardJavaFileManager standardJavaFileManager;
	private final List<String> options = new ArrayList<>();
	private final List<String> classpathList = new ArrayList<>();
	private final Collection<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
	private boolean isSaveSourceCodeFile;
	private boolean isSaveClassFile;
	private String generatorCodeSavePath;
	private DynamicClassLoader dynamicClassLoader;

	private final List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<Diagnostic<? extends JavaFileObject>>();
	private final List<Diagnostic<? extends JavaFileObject>> warnings = new ArrayList<Diagnostic<? extends JavaFileObject>>();

	public AsgcCompiler() {
		this(ClassLoader.getSystemClassLoader());
	}

	public AsgcCompiler(ClassLoader classLoader) {
		if (null == javaCompiler) {
			throw new RuntimeException("Can not load JavaCompiler from javax.tools.ToolProvider#getSystemJavaCompiler(),\n please confirm the application running in JDK not JRE.");
		}
		this.collector = new DiagnosticCollector<>();
		this.standardJavaFileManager = javaCompiler.getStandardFileManager(collector, null, null);
		this.isSaveClassFile = false;
		this.generatorCodeSavePath = GlobalConfig.getGeneratorCodeSavePath();
		this.dynamicClassLoader = new DynamicClassLoader(classLoader);

		addOption("-Xlint:unchecked");
		addOption("-source", "1.8");
		addOption("-target", "1.8");
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
		List<String> list = Lists.newArrayList(options);
		if (!CollectionUtil.isEmpty(classpathList)) {
			list.add("-classpath");
			list.add(classpathList.stream().collect(Collectors.joining(File.pathSeparator)));
		}
		return list;
	}

	private void addOption(String option) {
		this.options.add(option);
	}

	private void addOption(String key, String val) {
		this.options.add(key);
		this.options.add(val);
	}

	private void addSource(String className, String source) {
		addSource(new StringSource(className, source));
	}

	private void addSource(JavaFileObject javaFileObject) {
		compilationUnits.add(javaFileObject);
	}

	/**
	 * 编译代码
	 * @param className 类名
	 * @param sourceCode 源代码
	 */
	public Class<?> compile(String pkg, String className, String sourceCode) throws ClassNotFoundException {
		log.info("options:" + getOptions());
		JavaFileManager javaFileManager = new DynamicJavaFileManager(standardJavaFileManager, dynamicClassLoader);
		Boolean result = javaCompiler.getTask(null, javaFileManager, collector, getOptions(), null, Lists.newArrayList(new StringSource(className, sourceCode))).call();
		if (!result || collector.getDiagnostics().size() > 0) {
//			collector.getDiagnostics().forEach(item -> log.error(item.toString()));
			if (!result || collector.getDiagnostics().size() > 0) {
				for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
					switch (diagnostic.getKind()) {
						case NOTE:
						case MANDATORY_WARNING:
						case WARNING:
							warnings.add(diagnostic);
							break;
						case OTHER:
						case ERROR:
						default:
							errors.add(diagnostic);
							break;
					}
				}

				log.error("warring: {}", getWarnings());
				log.error("error: {}", getErrors());
			}
		}

		return dynamicClassLoader.findClass(pkg + "." + className);
	}

	private List<String> diagnosticToString(List<Diagnostic<? extends JavaFileObject>> diagnostics) {

		List<String> diagnosticMessages = new ArrayList<String>();

		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
			diagnosticMessages.add(
				"line: " + diagnostic.getLineNumber() + ", message: " + diagnostic.getMessage(Locale.US));
		}

		return diagnosticMessages;

	}

	public List<String> getErrors() {
		return diagnosticToString(errors);
	}

	public List<String> getWarnings() {
		return diagnosticToString(warnings);
	}
}
