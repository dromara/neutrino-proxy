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

import javax.tools.*;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
public class DynamicJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	private static final String[] superLocationNames = { StandardLocation.PLATFORM_CLASS_PATH.name(),
		/** JPMS StandardLocation.SYSTEM_MODULES **/
		"SYSTEM_MODULES" };
	private final PackageInternalsFinder finder;
	private final DynamicClassLoader classLoader;
	private final List<MemoryByteCode> byteCodes = new ArrayList<MemoryByteCode>();

	public DynamicJavaFileManager(JavaFileManager fileManager, DynamicClassLoader classLoader) {
		super(fileManager);
		this.classLoader = classLoader;
		this.finder = new PackageInternalsFinder(classLoader);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
											   JavaFileObject.Kind kind, FileObject sibling) throws IOException {

		for (MemoryByteCode byteCode : byteCodes) {
			if (byteCode.getClassName().equals(className)) {
				return byteCode;
			}
		}

		MemoryByteCode innerClass = new MemoryByteCode(className);
		byteCodes.add(innerClass);
		classLoader.registerCompiledSource(innerClass);
		return innerClass;

	}
	@Override
	public ClassLoader getClassLoader(JavaFileManager.Location location) {
		return classLoader;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof CustomJavaFileObject) {
			return ((CustomJavaFileObject) file).binaryName();
		} else {
			/**
			 * if it's not CustomJavaFileObject, then it's coming from standard file manager
			 * - let it handle the file
			 */
			return super.inferBinaryName(location, file);
		}
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
										 boolean recurse) throws IOException {
		if (location instanceof StandardLocation) {
			String locationName = ((StandardLocation) location).name();
			for (String name : superLocationNames) {
				if (name.equals(locationName)) {
					return super.list(location, packageName, kinds, recurse);
				}
			}
		}

		// merge JavaFileObjects from specified ClassLoader
		if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
			return new IterableJoin<>(super.list(location, packageName, kinds, recurse),
				finder.find(packageName));
		}

		return super.list(location, packageName, kinds, recurse);
	}

	static class IterableJoin<T> implements Iterable<T> {
		private final Iterable<T> first, next;

		public IterableJoin(Iterable<T> first, Iterable<T> next) {
			this.first = first;
			this.next = next;
		}

		@Override
		public Iterator<T> iterator() {
			return new IteratorJoin<T>(first.iterator(), next.iterator());
		}
	}

	static class IteratorJoin<T> implements Iterator<T> {
		private final Iterator<T> first, next;

		public IteratorJoin(Iterator<T> first, Iterator<T> next) {
			this.first = first;
			this.next = next;
		}

		@Override
		public boolean hasNext() {
			return first.hasNext() || next.hasNext();
		}

		@Override
		public T next() {
			if (first.hasNext()) {
				return first.next();
			}
			return next.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}
}
