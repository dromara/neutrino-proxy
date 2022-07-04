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

import fun.asgc.neutrino.core.aop.Invocation;
import fun.asgc.neutrino.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Asgc代理（子类代理）
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
@Slf4j
public class AsgcProxyFactory implements ProxyFactory {
	private static final String SYMBOLIC = "AsgcProxy$$";
	private static final String classNameTemplate = "%s" + SYMBOLIC + "%s";
	private static AtomicLong proxyClassCounter = new AtomicLong();
	private ProxyCompiler compiler = new ProxyCompiler();
	private ProxyClassLoader classLoader = new ProxyClassLoader();

	@Override
	public <T> T get(Class<T> clazz) throws Exception {
		Assert.notNull(clazz, "被代理类不能为空！");
		Assert.isTrue(canProxy(clazz), String.format("类[%s]无法被代理!", clazz.getName()));
		return doGet(clazz);
	}

	@Override
	public boolean canProxy(Class<?> clazz) {
		return ClassUtil.isInterface(clazz) ||
			(!ClassUtil.isFinal(clazz)
			&& !ClassUtil.isAbstract(clazz)
			&& ClassUtil.isPublic(clazz)
			&& !ClassUtil.isStatic(clazz)
			&& !ClassUtil.isInterface(clazz)
			&& Stream.of(clazz.getConstructors()).filter(e -> e.getParameterCount() == 0).count() > 0);
	}

	@Override
	public boolean isProxyClass(Class<?> clazz) {
		return null != clazz && clazz.getSimpleName().contains(SYMBOLIC);
	}

	private <T> T doGet(Class<T> clazz) throws ReflectiveOperationException {
		ProxyClass proxyClass = new ProxyClass(clazz);
		proxyClass.setName(generateClassName(clazz));
		String sourceCode = generateProxyClassSourceCode(clazz, proxyClass.getName());
		proxyClass.setSourceCode(sourceCode);
		log.debug("类:{} 的代理类源码:\n{}", clazz.getName(), sourceCode);

		compiler.compile(proxyClass);
		Class<T> retClass = (Class<T>)classLoader.loadProxyClass(proxyClass);
		T obj = retClass.newInstance();
		return obj;
	}

	private String generateClassName(Class<?> clazz) {
		return String.format(classNameTemplate, clazz.getSimpleName(), proxyClassCounter.incrementAndGet());
	}

	/**
	 * 生成代理类源代码 - 继承方式
	 * 1、类不能有final修饰符
	 * 2、被代理方法不能有final修饰符
	 * @param clazz
	 * @return
	 */
	private String generateProxyClassSourceCode(Class<?> clazz, String proxyClassName) {
		if (ClassUtil.isInterface(clazz)) {
			return generateProxyClassSourceCodeForInterface(clazz, proxyClassName);
		}
		Set<Class<?>> importClasses = new HashSet<>();
		StringBuilder header = new StringBuilder();
		header.append("package " + clazz.getPackage().getName() + ";").append("\n");
		appendImport(header, Invocation.class, importClasses);
		appendImport(header, ProxyCache.class, importClasses);

		StringBuilder body = new StringBuilder();
		body.append("public class ").append(proxyClassName).append(" extends ").append(clazz.getSimpleName()).append("{\n");
		Method[] methods = clazz.getMethods();
		if (ArrayUtil.notEmpty(methods)) {
			for (Method method : methods) {
				if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
					continue;
				}
				Long methodId = ProxyCache.setMethod(method);
				Class<?> returnType = method.getReturnType();
				Set<Class<?>> exceptionTypes = ReflectUtil.getExceptionTypes(method);
				boolean isVoid = returnType == void.class;
				boolean isThrow = CollectionUtil.notEmpty(exceptionTypes);
				String parametersString = buildParametersString(method.getParameters());
				String parameterNamesString = buildParameterNamesString(method.getParameters());
				String throwString = isThrow ? "throws " + buildTypeNameString(exceptionTypes) : "";
				if (isThrow) {
					for (Class<?> c : exceptionTypes) {
						appendImport(header, c, importClasses);
					}
				}

				body.append("\t").append("public").append(" ").append(returnType.getName()).append(" ").append(method.getName()).append("(").append(parametersString).append(") ").append(throwString).append(" {").append("\n")
					.append("\t\tInvocation inv = new Invocation(").append(methodId + "L,").append("this,").append("() -> {").append("\n")
					.append("\t\t\t").append(isVoid ? "" : "return ").append("super.").append(method.getName()).append("(").append(parameterNamesString).append(");").append("\n")
					.append(isVoid ? "\t\t\treturn null;\n" : "")
					.append("\t\t").append("}").append(StringUtil.isEmpty(parameterNamesString) ? "" : "," + parameterNamesString).append(");").append("\n")
					.append("\t\t").append("try {\n")
					.append("\t\t\t").append("inv.invoke();").append("\n")
					.append("\t\t").append("} catch (Exception e) {\n");

				if (isThrow) {
					body.append("\t\t\t").append("if (ProxyCache.checkMethodThrow(" + methodId + "L,e)) {").append("\n")
						.append("\t\t\t\t").append("throw e;").append("\n")
						.append("\t\t\t").append("} else {").append("\n")
						.append("\t\t\t\t").append("e.printStackTrace();").append("\n")
						.append("\t\t\t").append("}").append("\n");
				} else {
					body.append("\t\t\t").append("e.printStackTrace();").append("\n");
				}
				body.append("\t\t").append("}\n")
					.append(isVoid ? "" : "\t\treturn inv.getReturnValue();\n")
					.append("\t").append("}").append("\n");
			}
		}
		body.append("}").append("\n");
		return header.toString().concat(body.toString());
	}

	private String generateProxyClassSourceCodeForInterface(Class<?> clazz, String proxyClassName) {
		Set<Class<?>> importClasses = new HashSet<>();
		StringBuilder header = new StringBuilder();
		header.append("package " + clazz.getPackage().getName() + ";").append("\n");
		appendImport(header, Invocation.class, importClasses);
		appendImport(header, ProxyCache.class, importClasses);

		StringBuilder body = new StringBuilder();
		body.append("public class ").append(proxyClassName).append(" implements ").append(clazz.getSimpleName()).append("{\n");
		Method[] methods = clazz.getMethods();
		if (ArrayUtil.notEmpty(methods)) {
			for (Method method : methods) {
				if (Modifier.isFinal(method.getModifiers())) {
					continue;
				}
				Long methodId = ProxyCache.setMethod(method);
				Class<?> returnType = method.getReturnType();
				Set<Class<?>> exceptionTypes = ReflectUtil.getExceptionTypes(method);
				boolean isVoid = returnType == void.class;
				boolean isThrow = CollectionUtil.notEmpty(exceptionTypes);
				String parametersString = buildParametersString(method.getParameters());
				String parameterNamesString = buildParameterNamesString(method.getParameters());
				String throwString = isThrow ? "throws " + buildTypeNameString(exceptionTypes) : "";
				if (isThrow) {
					for (Class<?> c : exceptionTypes) {
						appendImport(header, c, importClasses);
					}
				}

				body.append("\t").append("public").append(" ").append(returnType.getName()).append(" ").append(method.getName()).append("(").append(parametersString).append(") ").append(throwString).append(" {").append("\n")
					.append("\t\tInvocation inv = new Invocation(").append(methodId + "L,").append("this,").append("() -> {").append("\n")
					.append("\t\t\treturn null;\n")
					.append("\t\t").append("}").append(StringUtil.isEmpty(parameterNamesString) ? "" : "," + parameterNamesString).append(");").append("\n")
					.append("\t\t").append("try {\n")
					.append("\t\t\t").append("inv.invoke();").append("\n")
					.append("\t\t").append("} catch (Exception e) {\n");

				if (isThrow) {
					body.append("\t\t\t").append("if (ProxyCache.checkMethodThrow(" + methodId + "L,e)) {").append("\n")
						.append("\t\t\t\t").append("throw e;").append("\n")
						.append("\t\t\t").append("} else {").append("\n")
						.append("\t\t\t\t").append("e.printStackTrace();").append("\n")
						.append("\t\t\t").append("}").append("\n");
				} else {
					body.append("\t\t\t").append("e.printStackTrace();").append("\n");
				}
				body.append("\t\t").append("}\n")
					.append(isVoid ? "" : "\t\treturn inv.getReturnValue();\n")
					.append("\t").append("}").append("\n");

			}
		}
		body.append("}").append("\n");
		return header.toString().concat(body.toString());
	}

	/**
	 * 追加import语句，java.lang包下不需要import
	 * @param sb
	 * @param clazz
	 * @param importClasses
	 */
	private synchronized void appendImport(StringBuilder sb, Class<?> clazz, Set<Class<?>> importClasses) {
		if (importClasses.contains(clazz) || clazz.getName().startsWith("java.lang.")) {
			return;
		}
		importClasses.add(clazz);
		sb.append("import ").append(clazz.getName()).append(";\n");
	}

	private String buildParametersString(Parameter[] parameters) {
		if (ArrayUtil.isEmpty(parameters)) {
			return "";
		}
		return Stream.of(parameters).map(item -> item.getType().getName() + " " + item.getName()).collect(Collectors.joining(","));
	}

	private String buildParameterNamesString(Parameter[] parameters) {
		if (ArrayUtil.isEmpty(parameters)) {
			return "";
		}
		return Stream.of(parameters).map(Parameter::getName).collect(Collectors.joining(","));
	}

	private String buildTypeNameString(Set<Class<?>> classes) {
		if (CollectionUtil.isEmpty(classes)) {
			return "";
		}
		return classes.stream().map(Class::getName).collect(Collectors.joining(","));
	}
}
