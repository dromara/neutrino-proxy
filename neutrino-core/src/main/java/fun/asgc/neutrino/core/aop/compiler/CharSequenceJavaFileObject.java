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
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
class CharSequenceJavaFileObject extends SimpleJavaFileObject {
	private final CharSequence sourceCode;
	private ByteArrayOutputStream out;

	public CharSequenceJavaFileObject(URI uri, Kind kind) {
		super(uri, kind);
		this.sourceCode = null;
	}

	public CharSequenceJavaFileObject(String name, String source) {
		super(URI.create(name + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
		this.sourceCode = source;
	}

	public CharSequenceJavaFileObject(String name, JavaFileObject.Kind kind) {
		super(URI.create(name + kind.extension), kind);
		this.sourceCode = null;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		if (this.sourceCode == null) {
			throw new IllegalStateException("源代码不能为空!");
		}
		return sourceCode;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		if (null == out) {
			this.out = new ByteArrayOutputStream();
		}
		return out;
	}

	public byte[] getByteCode() {
		if (null == out) {
			return null;
		}
		return out.toByteArray();
	}
}
