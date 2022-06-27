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
package fun.asgc.neutrino.core.db.template;

import fun.asgc.neutrino.core.base.Convert;
import fun.asgc.neutrino.core.util.StringUtil;

/**
 * 默认的列名转换器转换器
 * 代码 -> DB
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public class DefaultColumnNameConvert implements Convert<String, String> {
	private static final DefaultColumnNameConvert instance = new DefaultColumnNameConvert();
	private static final char SEPARATOR = '_';

	private DefaultColumnNameConvert() {

	}

	public static DefaultColumnNameConvert getInstance() {
		return instance;
	}

	@Override
	public String from(String target) {
		if (StringUtil.isEmpty(target)) {
			return "";
		}
		boolean flag = false;
		StringBuilder sb = new StringBuilder(target.length());
		for (char c : target.toCharArray()) {
			if (SEPARATOR == c) {
				flag = true;
			} else {
				if (flag) {
					sb.append(Character.toUpperCase(c));
					flag = false;
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String to(String source) {
		if (StringUtil.isEmpty(source)) {
			return "";
		}
		boolean flag = true;
		StringBuilder sb = new StringBuilder(source.length());
		for (char c : source.toCharArray()) {
			if (flag) {
				sb.append(Character.toLowerCase(c));
				flag = false;
			} else {
				if (Character.isUpperCase(c)) {
					sb.append(SEPARATOR);
					sb.append(Character.toLowerCase(c));
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}
}
