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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * 自定义java文件对象
 * @author: aoshiguchen
 * @date: 2022/8/25
 */
public class CustomJavaFileObject implements JavaFileObject {
	private final String binaryName;
	private final URI uri;
	private final String name;

	public CustomJavaFileObject(String binaryName, URI uri) {
		this.uri = uri;
		this.binaryName = binaryName;
		name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath(); // for FS based URI the path is not null, for JAR URI the scheme specific part is not null
	}

	@Override
	public URI toUri() {
		return uri;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		InputStream in = SpringBootJarParser.getInputStream(uri);
		if (null != in) {
			return in;
		}
		return uri.toURL().openStream();
	}

	@Override
	public OutputStream openOutputStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Reader openReader(boolean ignoreEncodingErrors) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Writer openWriter() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public boolean delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Kind getKind() {
		return Kind.CLASS;
	}

	@Override
	public boolean isNameCompatible(String simpleName, Kind kind) {
		String baseName = simpleName + kind.extension;
		return kind.equals(getKind())
			&& (baseName.equals(getName())
			|| getName().endsWith("/" + baseName));
	}

	@Override
	public NestingKind getNestingKind() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Modifier getAccessLevel() {
		throw new UnsupportedOperationException();
	}

	public String binaryName() {
		return binaryName;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + this.toUri() + "]";
	}
}
