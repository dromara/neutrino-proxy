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

import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class DateUtil {
	private static final Cache<String, SimpleDateFormat> sdfCache = new MemoryCache<>();
	private static SimpleDateFormat getSimpleDateFormat(String format) {
		return LockUtil.doubleCheckProcess(
			() -> !sdfCache.containsKey(format),
			format,
			() -> sdfCache.set(format, new SimpleDateFormat(format)),
			() -> sdfCache.get(format)
		);
	}

	/**
	 * 日期格式化
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		try {
			return getSimpleDateFormat(format).format(date);
		} catch (Exception e) {
			// ignore
		}
		return "";
	}
}
