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

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/25
 */
public class PackageInternalsFinder {
	private final ClassLoader classLoader;
	private static final String CLASS_FILE_EXTENSION = ".class";

	public PackageInternalsFinder(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public List<JavaFileObject> find(String packageName) throws IOException {
		String javaPackageName = packageName.replaceAll("\\.", "/");

		List<JavaFileObject> result = new ArrayList<>();

		Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
		while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
			URL packageFolderURL = urlEnumeration.nextElement();
			result.addAll(listUnder(packageName, packageFolderURL));
		}

		return result;
	}

	private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
		File directory = new File(decode(packageFolderURL.getFile()));
		if (directory.isDirectory()) { // browse local .class files - useful for local execution
			return processDir(packageName, directory);
		} else { // browse a jar file
			return processJar(packageFolderURL);
		} // maybe there can be something else for more involved class loaders
	}

	private List<JavaFileObject> processJar(URL packageFolderURL) {
		List<JavaFileObject> result = new ArrayList<JavaFileObject>();
		try {
			String jarUri = packageFolderURL.toExternalForm().substring(0, packageFolderURL.toExternalForm().lastIndexOf("!/"));

			JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
			String rootEntryName = jarConn.getEntryName();
			int rootEnd = rootEntryName.length() + 1;

			Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
			while (entryEnum.hasMoreElements()) {
				JarEntry jarEntry = entryEnum.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) {
					URI uri = URI.create(jarUri + "!/" + name);
					String binaryName = name.replaceAll("/", ".");
					binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

					result.add(new CustomJavaFileObject(binaryName, uri));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
		}
		return result;
	}

	private List<JavaFileObject> processDir(String packageName, File directory) {
		List<JavaFileObject> result = new ArrayList<JavaFileObject>();

		File[] childFiles = directory.listFiles();
		if (childFiles != null) {
			for (File childFile : childFiles) {
				if (childFile.isFile()) {
					// We only want the .class files.
					if (childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
						String binaryName = packageName + "." + childFile.getName();
						binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

						result.add(new CustomJavaFileObject(binaryName, childFile.toURI()));
					}
				}
			}
		}

		return result;
	}

	private String decode(String filePath) {
		try {
			return URLDecoder.decode(filePath, "utf-8");
		} catch (Exception e) {
			// ignore, return original string
		}

		return filePath;
	}
}
