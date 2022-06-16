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

package fun.asgc.neutrino.core.constant;

/**
 * 类型不匹配值（后期计划改为"类型距离"）
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface TypeNotMatchingValueConstant {

	/**
	 * 最小值，表示完全匹配
	 */
	int MIN = 0;

	/**
	 * 最大值，表示完全不匹配
	 */
	int MAX = Integer.MAX_VALUE;

	/**
	 * 完全匹配
	 */
	int COMPLETE = MIN;

	/**
	 * 完全不匹配
	 */
	int NOT = MAX;

	/**
	 * 包装匹配
	 */
	int WRAP = 1;

	/**
	 * 解包装匹配
	 */
	int UNWRAP = 2;

	/**
	 * 超类匹配起始值
	 * 每增加1层，值加1
	 */
	int SUPER_CLASS_MIN = 100000;

	/**
	 * 超类最大层级（假定继承层级不会超过40万）
	 */
	int SUPER_CLASS_MAX_LEVEL = 400000;

	/**
	 * 超类匹配结束值
	 */
	int SUPER_CLASS_MAX = SUPER_CLASS_MIN + SUPER_CLASS_MAX_LEVEL - 1;

	/**
	 * 类型提升起始值
	 */
	int ASCENSION_MIN = 600000;

	/**
	 * 类型提升层级
	 */
	int ASCENSION_LEVEL = 10000;

	/**
	 * 类型提升最大值
	 */
	int ASCENSION_MAX = ASCENSION_MIN + ASCENSION_LEVEL - 1;

	/**
	 * 自定义类型转换起始值
	 */
	int CUSTOM_MIN = 10000000;

	/**
	 * 自定义类型转换层级
	 */
	int CUSTOM_LEVEL = 10000000;

	/**
	 * 自定义类型转换结束值
	 */
	int CUSTOM_MAX = CUSTOM_MIN + CUSTOM_LEVEL - 1;
}
