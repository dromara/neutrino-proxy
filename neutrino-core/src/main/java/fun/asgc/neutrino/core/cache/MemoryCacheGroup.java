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

import fun.asgc.neutrino.core.util.LockUtil;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class MemoryCacheGroup<K,V> implements CacheGroup<K,V> {

	/**
	 * 缓存
	 */
	private final Cache<String,Cache<K,V>> cacheGroup = new MemoryCache<>();

	@Override
	public Cache<K, V> getCache(String group) {
		return cacheGroup.get(group);
	}

	@Override
	public void setCache(String group, Cache<K, V> cache) {
		cacheGroup.set(group, cache);
	}

	@Override
	public boolean containsCache(String group) {
		return cacheGroup.containsKey(group);
	}

	@Override
	public void set(String group, K k, V v) {
		LockUtil.doubleCheckProcess(() -> !cacheGroup.containsKey(group),
			group,
			() -> cacheGroup.set(group, new MemoryCache<>())
		);
		cacheGroup.get(group).set(k, v);
	}

	@Override
	public V get(String group, K k) {
		return (cacheGroup.containsKey(group) && cacheGroup.get(group).containsKey(k)) ? cacheGroup.get(group).get(k) : null;
	}

	@Override
	public boolean containsKey(String group, K k) {
		return cacheGroup.containsKey(group) && cacheGroup.get(group).containsKey(k);
	}

	@Override
	public boolean isEmpty(String group) {
		return !cacheGroup.containsKey(group) || cacheGroup.get(group).isEmpty();
	}
}
