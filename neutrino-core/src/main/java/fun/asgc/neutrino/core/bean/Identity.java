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
package fun.asgc.neutrino.core.bean;

/**
 * Identity 用于标识一个身份。
 *
 * 当`isOnly()`为true时，该标识是一个唯一标识。
 *
 * 如：身份证、财富、年龄都可以作为人的身份象征，但只有身份证是唯一标识。
 * 这个唯一性取决与理论上的定义，而非实际数据。
 * 例如有三个人，年龄分别是18、19、20，此时对于这三人而言，年龄是唯一的，但年龄这个标识我们仍然认为是非唯一标识。
 * 除非在特定场景下，这些数据固定不变、或者确保以后新增的数据也不会打破这一设定。
 *
 * @author: aoshiguchen
 * @date: 2022/7/1
 */
public interface Identity {
	/**
	 * 是否唯一标识
	 * @return
	 */
	boolean isOnly();
}
