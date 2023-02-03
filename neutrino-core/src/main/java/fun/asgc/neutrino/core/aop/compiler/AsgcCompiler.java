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
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * asgc编译器
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
	private final List<String> defaultClassPathList = new ArrayList<>();
	private final List<String> classpathList = new ArrayList<>();
	private final Collection<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
	private boolean isSaveSourceCodeFile;
	private boolean isSaveClassFile;
	private String generatorCodeSavePath;
	private DynamicClassLoader dynamicClassLoader;

	private final List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<Diagnostic<? extends JavaFileObject>>();
	private final List<Diagnostic<? extends JavaFileObject>> warnings = new ArrayList<Diagnostic<? extends JavaFileObject>>();

	/**
	 * 构造编译器
	 */
	public AsgcCompiler() {
		this(ClassLoader.getSystemClassLoader());
	}

	/**
	 * 构造编译器
	 * @param classLoader
	 */
	public AsgcCompiler(ClassLoader classLoader) {
		if (null == javaCompiler) {
			throw new RuntimeException("Can not load JavaCompiler from javax.tools.ToolProvider#getSystemJavaCompiler(),\n please confirm the application running in JDK not JRE.");
		}
		this.collector = new DiagnosticCollector<>();
		this.standardJavaFileManager = javaCompiler.getStandardFileManager(collector, null, null);
		this.isSaveClassFile = false;
		this.generatorCodeSavePath = GlobalConfig.getGeneratorCodeSavePath();
		this.dynamicClassLoader = new DynamicClassLoader(classLoader, this);

		addOption("-Xlint:unchecked");
		addOption("-implicit:class");
		addOption("-source", "1.8");
		addOption("-target", "1.8");
	}

	/**
	 * 添加类路径
	 * @param classpath
	 */
	public void addClasspath(String classpath) {
		if (this.classpathList.contains(classpath)) {
			return;
		}
		this.classpathList.add(classpath);
	}

	/**
	 * 设置是否保存类文件
	 * @param saveClassFile
	 */
	public void setSaveClassFile(boolean saveClassFile) {
		this.isSaveClassFile = saveClassFile;
	}

	/**
	 * 设置是否保存源代码文件
	 * @param saveSourceCodeFile
	 */
	public void setSaveSourceCodeFile(boolean saveSourceCodeFile) {
		isSaveSourceCodeFile = saveSourceCodeFile;
	}

	/**
	 * 设置保存代码路径
	 * @param generatorCodeSavePath
	 */
	public void setGeneratorCodeSavePath(String generatorCodeSavePath) {
		this.generatorCodeSavePath = generatorCodeSavePath;
	}

	/**
	 * 获取options
	 * @return
	 */
	private List<String> getOptions() {
		List<String> list = Lists.newArrayList(options);
		List<String> cp = getClasspathList();
		if (!CollectionUtil.isEmpty(cp)) {
			list.add("-classpath");
			list.add(cp.stream().collect(Collectors.joining(File.pathSeparator)));
		}
		return list;
	}

	/**
	 * 添加option
	 * @param option
	 */
	private void addOption(String option) {
		this.options.add(option);
	}

	/**
	 * 添加option
	 * @param key
	 * @param val
	 */
	private void addOption(String key, String val) {
		this.options.add(key);
		this.options.add(val);
	}

	/**
	 * 添加源代码
	 * @param className
	 * @param source
	 */
	private void addSource(String className, String source) {
		addSource(new StringSource(className, source));
	}

	/**
	 * 添加源代码
	 * @param javaFileObject
	 */
	private void addSource(JavaFileObject javaFileObject) {
		compilationUnits.add(javaFileObject);
	}

	/**
	 * 编译代码
	 * @param pkg 包名
	 * @param className 类名
	 * @param sourceCode 源代码
	 */
	public Class<?> compile(String pkg, String className, String sourceCode) throws ClassNotFoundException {
		log.info("options:" + getOptions());
		JavaFileManager javaFileManager = new DynamicJavaFileManager(standardJavaFileManager, dynamicClassLoader);
		Iterable<? extends JavaFileObject> compilationUnits = Lists.newArrayList(new StringSource(className, sourceCode));
		if (GlobalConfig.isSaveGeneratorCode()) {
			javaFileManager = standardJavaFileManager;
			File file = FileUtil.save(GlobalConfig.getGeneratorCodeSavePath() + pkg.replaceAll("\\.", "/"), className + ".java", sourceCode);
			compilationUnits = standardJavaFileManager.getJavaFileObjects(file);
			addClasspath(GlobalConfig.getGeneratorCodeSavePath());
		}

		Boolean result = javaCompiler.getTask(null, javaFileManager, collector, getOptions(), null, compilationUnits).call();
		if (!result || collector.getDiagnostics().size() > 0) {
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

				log();
			}
		}

		return dynamicClassLoader.findClass(pkg + "." + className);
	}

	/**
	 * 获取编译诊断信息
	 * @param diagnostics
	 * @return
	 */
	private List<String> diagnosticToString(List<Diagnostic<? extends JavaFileObject>> diagnostics) {

		List<String> diagnosticMessages = new ArrayList<String>();

		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
			diagnosticMessages.add(
				"line: " + diagnostic.getLineNumber() + ", message: " + diagnostic.getMessage(Locale.US));
		}

		return diagnosticMessages;

	}

	/**
	 * 获取异常信息
	 * @return
	 */
	public List<String> getErrors() {
		return diagnosticToString(errors);
	}

	/**
	 * 获取警告信息
	 * @return
	 */
	public List<String> getWarnings() {
		return diagnosticToString(warnings);
	}

	/**
	 * 打印编译日志
	 */
	private void log() {
		List<String> warnings = getWarnings();
		List<String> errors = getErrors();
//		if (!CollectionUtil.isEmpty(warnings)) {
//			log.warn(warnings.stream().collect(Collectors.joining()));
//		}
		if (!CollectionUtil.isEmpty(errors)) {
			log.error(errors.stream().collect(Collectors.joining()));
		}
	}

	/**
	 * 获取URL类路径加载器
	 * @return
	 */
	private URLClassLoader getURLClassLoader() {
		ClassLoader ret = Thread.currentThread().getContextClassLoader();
		if (null == ret) {
			ret = AsgcCompiler.class.getClassLoader();
		}
		return (ret instanceof URLClassLoader) ? (URLClassLoader)ret : null;
	}

	/**
	 * 获取类路径列表
	 * @return
	 */
	public List<String> getClasspathList() {
		List<String> classpathList = new ArrayList<>();
		List<String> defaultClasspathList = getDefaultClasspathList();
		List<String> customClasspathList = this.classpathList;
		if (!CollectionUtil.isEmpty(defaultClasspathList)) {
			classpathList.addAll(defaultClasspathList);
		}
		if (!CollectionUtil.isEmpty(customClasspathList)) {
			classpathList.addAll(customClasspathList);
		}
		return classpathList;
	}

	/**
	 * 获取默认的类路径列表
	 * @return
	 */
	private synchronized List<String> getDefaultClasspathList() {
		if (!CollectionUtil.isEmpty(defaultClassPathList)) {
			return defaultClassPathList;
		}
		URLClassLoader classLoader = getURLClassLoader();
		if (null == classLoader) {
			return defaultClassPathList;
		}

		boolean isWindows = SystemUtil.isWindows();
		for (URL url : classLoader.getURLs()) {
			String path = url.getFile();

			// 如果是 windows 系统，去除前缀字符 '/'
			if (isWindows && path.startsWith("/")) {
				path = path.substring(1);
			}

			// 去除后缀字符 '/'
			if (path.length() > 1 && (path.endsWith("/") || path.endsWith(File.separator))) {
				path = path.substring(0, path.length() - 1);
			}

			defaultClassPathList.add(path);
		}

		return defaultClassPathList;
	}
}
