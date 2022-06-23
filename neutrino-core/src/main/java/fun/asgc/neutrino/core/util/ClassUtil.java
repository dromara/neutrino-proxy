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

package fun.asgc.neutrino.core.util;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class ClassUtil {

	private static Cache<String, Set<Class<?>>> classesCache = new MemoryCache<>();
	private static Cache<String, Set<Class<?>>> componentsCache = new MemoryCache<>();

	/**
	 * 类扫描
	 * @param packageName
	 * @return
	 */
	public static Set<Class<?>> scanComponents(String packageName) throws IOException, ClassNotFoundException {
		if (StringUtil.isEmpty(packageName)) {
			return null;
		}
		if (componentsCache.containsKey(packageName)) {
			componentsCache.get(packageName);
		}

		Set<Class<?>> result = new HashSet<>();
		Set<Class<?>> classes = scan(packageName);
		for (Class<?> clazz : classes) {
			if (isAnnotateWith(clazz, Component.class)) {
				if (!(clazz.isAnnotation() || clazz.isEnum())) {
					log.debug("scan component {}", clazz.getName());
					result.add(clazz);
				}
			}
		}
		componentsCache.set(packageName, result);
		return result;
	}

	/**
	 * 类扫描
	 * @param packages
	 * @return
	 */
	public static Set<Class<?>> scanComponents(Collection<String> packages) throws IOException, ClassNotFoundException {
		if (CollectionUtil.isEmpty(packages)) {
			return null;
		}
		Set<Class<?>> classes = new HashSet<>();
		for (String packageName : packages) {
			Set<Class<?>> tmp = scanComponents(packageName);
			if (CollectionUtil.notEmpty(tmp)) {
				classes.addAll(tmp);
			}
		}
		return classes;
	}

	public static Set<Class<?>> scan(List<String> packageNames) throws IOException, ClassNotFoundException{
		Set<Class<?>> result = new HashSet<>();
		if (null != packageNames && packageNames.size() > 0) {
			for (String packageName : packageNames) {
				Set<Class<?>> tmp = scan(packageName);
				if (null != tmp) {
					result.addAll(tmp);
				}

			}
		}
		return result;
	}

	/**
	 * 类扫描
	 * @param packageName
	 * @return
	 */
	public static Set<Class<?>> scan(String packageName) throws IOException, ClassNotFoundException {
		if (StringUtil.isEmpty(packageName)) {
			return null;
		}
		if (classesCache.containsKey(packageName)) {
			return classesCache.get(packageName);
		}
		log.info("开始进行类扫描，扫描包：{}", packageName);
		Set<Class<?>> classes = new HashSet<>();
		String packagePath = packageName.replace(".", "/");
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("");
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
			log.info(url.toString());
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
						log.debug("scan class {}", clazz.getName());
						classes.add(clazz);
					}
				}
			} else if ("file".endsWith(protocol)) {
				String path = url.getPath();
				String targetPath = path + "/" + packagePath;
				addClasses(targetPath, classes, packageName);
			}
		}
		log.info("扫描完毕，包：{}下一共有:{}个类", packageName, classes.size());
		classesCache.set(packageName, classes);
		return classes;
	}

	private static void addClasses(String path, Set<Class<?>> classes, String packageName) throws ClassNotFoundException {
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
				Class clazz = Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
				log.debug("scan class {}", clazz.getName());
				classes.add(clazz);
			} else {
				String subPackagePath = path + "/" + fileName;
				String subPackageName = packageName + "." + fileName;
				addClasses(subPackagePath, classes, subPackageName);
			}
		}
	}

	public static boolean isAnnotateWith(Class<?> clazz, Class targetAnnotation) {
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> aClass = annotation.annotationType();
			if (aClass.equals(targetAnnotation)) {
				return true;
			}
			if (aClass.isAnnotationPresent(targetAnnotation)) {
				return true;
			}
		}
		return false;
	}

	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
		T annotation = clazz.getAnnotation(annotationType);
		if (null == annotation) {
			Annotation[] annotations = clazz.getAnnotations();
			for (Annotation item : annotations) {
				Class<? extends Annotation> aClass = item.annotationType();
				if (aClass.isAnnotationPresent(annotationType)) {
					return aClass.getAnnotation(annotationType);
				}
			}
		}
		return annotation;
	}

	public static boolean isPublic(Class<?> clazz) {
		return Modifier.isPublic(clazz.getModifiers());
	}

	public static boolean isPrivate(Class<?> clazz) {
		return Modifier.isPrivate(clazz.getModifiers());
	}

	public static boolean isProtected(Class<?> clazz) {
		return Modifier.isProtected(clazz.getModifiers());
	}

	public static boolean isFinal(Class<?> clazz) {
		return Modifier.isFinal(clazz.getModifiers());
	}

	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	public static boolean isStatic(Class<?> clazz) {
		return Modifier.isStatic(clazz.getModifiers());
	}

	public static boolean isInterface(Class<?> clazz) {
		return clazz.isInterface();
	}
}
