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
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/6
 */
@SuppressWarnings("all")
public class AsgcProxyGenerator {
	private static final AsgcProxyGenerator instance = new AsgcProxyGenerator();
	private VelocityEngine engine;
	private Template template;

	private AsgcProxyGenerator() {
		engine = new VelocityEngine();
		engine.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine.init();
		template = engine.getTemplate("tpl/AsgcProxy.tpl");
	}

	private String render(Map<String, Object> map) {
		VelocityContext context = new VelocityContext(map);
		StringWriter stringWriter = new StringWriter();
		template.merge(context, stringWriter);
		return stringWriter.toString();
	}

	public String generator(String proxyClassName, Class<?> targetType) {
		Map<String, Object> map = new HashMap<>();
		map.put("package", targetType.getPackage().getName());
		List<Class<?>> importList = new ArrayList<>();
		map.put("importList", importList);
		appendImport(importList, Invocation.class);
		appendImport(importList, ProxyCache.class);
		map.put("proxyClassName", proxyClassName);
		map.put("targetType", targetType);
		map.put("targetIsInterface", ClassUtil.isInterface(targetType));

		List<MethodInfo> methodInfoList = new ArrayList<>();
		map.put("methodInfoList", methodInfoList);

		Method[] methods = targetType.getMethods();
		if (ArrayUtil.notEmpty(methods)) {
			for (Method method : methods) {
				if (Modifier.isFinal(method.getModifiers()) ||
					Modifier.isStatic(method.getModifiers()) ||
					method.isSynthetic()
				) {
					continue;
				}
				Long methodId = ProxyCache.setMethod(method);
				Class<?> returnType = method.getReturnType();
				boolean isVoid = returnType == void.class;
				appendImport(importList, returnType);
				if (ArrayUtil.notEmpty(method.getParameters())) {
					Stream.of(method.getParameters()).forEach(parameter -> appendImport(importList, parameter.getType()));
				}
				Set<Class<?>> exceptionTypes = ReflectUtil.getExceptionTypes(method);
				boolean isThrow = CollectionUtil.notEmpty(exceptionTypes);
				String throwsString = isThrow ? " throws " + buildTypeNameString(exceptionTypes) + " ": " ";
				if (CollectionUtil.notEmpty(exceptionTypes)) {
					exceptionTypes.forEach(exceptionType -> appendImport(importList, exceptionType));
				}

				String parameterNamesString = buildParameterNamesString(method.getParameters());
				methodInfoList.add(new MethodInfo()
					.setMethodId(methodId)
					.setReturnType(returnType)
					.setVoid(isVoid)
					.setMethodName(method.getName())
					.setParametersString(buildParametersString(method.getParameters()))
					.setThrowsString(throwsString)
					.setThrow(isThrow)
					.setParameterNamesString(parameterNamesString)
				);
			}
		}
		return render(map);
	}

	public static AsgcProxyGenerator getInstance() {
		return instance;
	}

	private String buildParametersString(Parameter[] parameters) {
		if (ArrayUtil.isEmpty(parameters)) {
			return "";
		}
		return Stream.of(parameters).map(item -> item.getType().getSimpleName() + " " + item.getName()).collect(Collectors.joining(", "));
	}

	private String buildTypeNameString(Set<Class<?>> classes) {
		if (CollectionUtil.isEmpty(classes)) {
			return "";
		}
		return classes.stream().map(Class::getSimpleName).collect(Collectors.joining(","));
	}

	private String buildParameterNamesString(Parameter[] parameters) {
		if (ArrayUtil.isEmpty(parameters)) {
			return "";
		}
		return Stream.of(parameters).map(Parameter::getName).collect(Collectors.joining(", "));
	}

	/**
	 * 追加import语句，java.lang包下不需要import
	 * @param importClasses
	 * @param clazz
	 */
	private synchronized void appendImport(List<Class<?>> importClasses, Class<?> clazz) {
		if (importClasses.contains(clazz) ||
			clazz.getName().startsWith("java.lang.") ||
			TypeUtil.isNormalBasicType(clazz) ||
			clazz == void.class
		) {
			return;
		}
		if (clazz.isArray()) {
			appendImport(importClasses, clazz.getComponentType());
			return;
		}

		importClasses.add(clazz);
	}

	@Accessors(chain = true)
	@Data
	public static class MethodInfo {
		private Long methodId;
		private Class<?> returnType;
		private boolean isVoid;
		private String methodName;
		private String parametersString;
		private String throwsString;
		private boolean isThrow;
		private String parameterNamesString;
	}
}
