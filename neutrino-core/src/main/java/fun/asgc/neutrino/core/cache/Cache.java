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

package fun.asgc.neutrino.core.cache;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface Cache<K,V> {

	/**
	 * 设置缓存
	 * @param k
	 * @param v
	 */
	void set(K k, V v);

	/**
	 * 获取缓存
	 * @param k
	 * @return
	 */
	V get(K k);

	/**
	 * 判断缓存是否存在
	 * @param k
	 * @return
	 */
	boolean containsKey(K k);

	/**
	 * 判断缓存值是否存在
	 * @param v
	 * @return
	 */
	boolean containsValue(V v);

	/**
	 * key集合
	 * @return
	 */
	Set<K> keySet();

	/**
	 * 值集合
	 * @return
	 */
	Collection<V> values();

	/**
	 * 是否为空
	 * @return
	 */
	boolean isEmpty();

	/**
	 * 清空缓存
	 */
	void clear();

	/**
	 * 缓存大小
	 * @return
	 */
	int size();

	/**
	 * 如果有且只有一个缓存，则返回该缓存值，否则返回空
	 * @return
	 */
	V isOnePeek();
}
