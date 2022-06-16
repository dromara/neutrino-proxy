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

import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class MemoryCache<K,V> implements Cache<K,V> {

	private Map<K,V> cache = Collections.synchronizedMap(new HashMap<>());

	@Override
	public void set(K k, V v) {
		cache.put(k, v);
	}

	@Override
	public V get(K k) {IdentityHashMap a = new IdentityHashMap();
		return (V)cache.get(k);
	}

	@Override
	public boolean containsKey(K k) {
		return cache.containsKey(k);
	}

	@Override
	public boolean containsValue(V v) {
		return cache.containsValue(v);
	}

	@Override
	public Set<K> keySet() {
		return cache.keySet();
	}

	@Override
	public Collection<V> values() {
		return cache.values();
	}

	@Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public V isOnePeek() {
		if (size() != 1) {
			return null;
		}
 		return cache.values().stream().findFirst().get();
	}
}
