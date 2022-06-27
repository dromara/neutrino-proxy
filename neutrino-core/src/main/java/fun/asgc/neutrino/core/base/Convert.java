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

package fun.asgc.neutrino.core.base;

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.util.CollectionUtil;

import java.util.List;

/**
 * 转换器
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public interface Convert<S,T> {

	/**
	 * 从目标内容转换为原内容
	 * @param target
	 * @return
	 */
	S from(T target);

	/**
	 * 从原内容转换为目标内容
	 * @param source
	 * @return
	 */
	T to(S source);

	/**
	 * 从目标内容转换为原内容
	 * @param targetList
	 * @return
	 */
	default List<S> from(List<T> targetList) {
		List<S> list = Lists.newArrayList();
		if (CollectionUtil.isEmpty(targetList)) {
			return list;
		}
		for (T target : targetList) {
			list.add(from(target));
		}
		return list;
	}

	/**
	 * 从原内容转换为目标内容
	 * @param sourceList
	 * @return
	 */
	default List<T> to(List<S> sourceList) {
		List<T> list = Lists.newArrayList();
		if (CollectionUtil.isEmpty(sourceList)) {
			return list;
		}
		for (S source : sourceList) {
			list.add(to(source));
		}
		return list;
	}
}
