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

package fun.asgc.neutrino.core.type;

/**
 * 类型距离
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface TypeDistance {

	/**
	 * 最小值，表示完全匹配
	 */
	int MIN = TypeMatchLevel.NULL.getDistanceMin();

	/**
	 * 最大值，表示完全不匹配
	 */
	int MAX = Integer.MAX_VALUE;

	/**
	 * 空匹配
	 */
	int NULL = MIN;

	/**
	 * 完全匹配
	 */
	int PERFECT = TypeMatchLevel.PERFECT.getDistanceMin();

	/**
	 * 完全不匹配
	 */
	int NOT = MAX;

	/**
	 * 包装匹配
	 */
	int PACKING = TypeMatchLevel.PACKING.getDistanceMin();

	/**
	 * 解包装匹配
	 */
	int UNPACKING = TypeMatchLevel.UNPACKING.getDistanceMin();

	/**
	 * 类型提升起始值
	 */
	int ASCENSION_MIN = TypeMatchLevel.ASCENSION.getDistanceMin();

	/**
	 * 类型提升最大值
	 */
	int ASCENSION_MAX = TypeMatchLevel.ASCENSION.getDistanceMax();

	/**
	 * 超类匹配起始值
	 * 每增加1层，值加1
	 */
	int SUPER_MIN = TypeMatchLevel.SUPER.getDistanceMin();

	/**
	 * 超类匹配结束值
	 */
	int SUPER_MAX = TypeMatchLevel.SUPER.getDistanceMax();

	/**
	 * 内部扩展起始值
	 */
	int EXTENSION_MIN = TypeMatchLevel.EXTENSION.getDistanceMin();

	/**
	 * 内部扩展结束值
	 */
	int EXTENSION_MAX = TypeMatchLevel.EXTENSION.getDistanceMax();

	/**
	 * 自定义类型转换起始值
	 */
	int CUSTOM_MIN = TypeMatchLevel.CUSTOM.getDistanceMin();

	/**
	 * 自定义类型转换结束值
	 */
	int CUSTOM_MAX = TypeMatchLevel.CUSTOM.getDistanceMax();
}
