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

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/25
 */
public class MemoryByteCode extends SimpleJavaFileObject {
	private static final char PKG_SEPARATOR = '.';
	private static final char DIR_SEPARATOR = '/';
	private static final String CLASS_FILE_SUFFIX = ".class";

	private ByteArrayOutputStream byteArrayOutputStream;
	private byte[] byteCode;

	public MemoryByteCode(String className) {
		super(URI.create("byte:///" + className.replace(PKG_SEPARATOR, DIR_SEPARATOR)
			+ Kind.CLASS.extension), Kind.CLASS);
	}

	public MemoryByteCode(String className, ByteArrayOutputStream byteArrayOutputStream)
		throws URISyntaxException {
		this(className);
		this.byteArrayOutputStream = byteArrayOutputStream;
	}

	public MemoryByteCode(String className, byte[] byteCode)
			throws URISyntaxException {
		this(className);
		this.byteCode = byteCode;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		if (byteArrayOutputStream == null) {
			byteArrayOutputStream = new ByteArrayOutputStream();
		}
		return byteArrayOutputStream;
	}

	public byte[] getByteCode() {
		return null == byteCode ? byteArrayOutputStream.toByteArray() : byteCode;
	}

	public String getClassName() {
		String className = getName();
		className = className.replace(DIR_SEPARATOR, PKG_SEPARATOR);
		className = className.substring(1, className.indexOf(CLASS_FILE_SUFFIX));
		return className;
	}

}
