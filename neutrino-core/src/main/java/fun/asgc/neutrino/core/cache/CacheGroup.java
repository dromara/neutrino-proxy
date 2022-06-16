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

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface CacheGroup<K,V> {

	/**
	 * 获取缓存
	 * @param group
	 * @return
	 */
	Cache<K,V> getCache(String group);

	/**
	 * 设置缓存
	 * @param group
	 * @param cache
	 */
	void setCache(String group, Cache<K, V> cache);

	/**
	 * 判断缓存是否存在
	 * @param group
	 * @return
	 */
	boolean containsCache(String group);

	/**
	 * 设置缓存值
	 * @param group
	 * @param k
	 * @param v
	 */
	void set(String group, K k, V v);

	/**
	 * 获取缓存值
	 * @param group
	 * @param k
	 * @return
	 */
	V get(String group, K k);

	/**
	 * 判断缓存值是否存在
	 * @param group
	 * @param k
	 * @return
	 */
	boolean containsKey(String group, K k);

	/**
	 * 是否为空
	 * @param group
	 * @return
	 */
	boolean isEmpty(String group);

}
