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

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class NumberUtil {

	/**
	 * 大小单位
	 */
	private static final List<String> SIZE_UINT = Lists.newArrayList("B", "K", "M", "G", "T");
	/**
	 * 单位容量
	 */
	private static final int UINT_CAPACITY = 1024;

	/**
	 * 大小转为描述
	 * @param size
	 * @return
	 */
	public static String sizeToDescription(long size, int decimals) {
		double num = size * 1.0;
		int unitIndex = 0;

		while (unitIndex < SIZE_UINT.size() - 1 && num >= UINT_CAPACITY) {
			num /= UINT_CAPACITY;
			unitIndex++;
		}

		return trimZero(String.format("%." + decimals + "f", num)) + SIZE_UINT.get(unitIndex);
	}

	/**
	 * 大小转为描述
	 * @param size
	 * @return
	 */
	public static String sizeToDescription(long size) {
		return sizeToDescription(size, 2);
	}

	public static long descriptionToSize(String description, long defaultValue) {
		try {
			description = description.trim().replaceAll(" ", "").toUpperCase();
			double num = Double.valueOf(description.substring(0, description.length() - 1));
			String unit = description.substring(description.length() - 1);
			int unitIndex = SIZE_UINT.indexOf(unit);
			if (unitIndex > 0) {
				while (unitIndex-- > 0) {
					num *= UINT_CAPACITY;
				}
			}
			return (long) num;
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public static long descriptionToSize(String description) {
		return descriptionToSize(description, 0);
	}

	private static String trimZero(String s) {
		if (s.indexOf(".") > 0) {
			// 去掉多余的0
			s = s.replaceAll("0+?$", "");
			// 如最后一位是.则去掉
			s = s.replaceAll("[.]$", "");
		}
		return s;
	}

}
