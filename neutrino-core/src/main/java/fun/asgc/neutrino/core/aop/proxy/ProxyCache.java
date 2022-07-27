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
package fun.asgc.neutrino.core.aop.proxy;

import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 代理缓存
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
public class ProxyCache {
	private static final AtomicLong methodId = new AtomicLong();
	private static final Map<Long, Method> methodCache = Collections.synchronizedMap(new HashMap<>());
	private static final Map<Long, Set<Class<?>>> methodExceptionTypesCache = Collections.synchronizedMap(new HashMap<>());

	public static Long generateMethodId() {
		return methodId.incrementAndGet();
	}

	public static Long setMethod(Method method) {
		Long id = generateMethodId();
		methodCache.put(id, method);
		methodExceptionTypesCache.put(id, ReflectUtil.getExceptionTypes(method));
		return id;
	}

	public static Method getMethod(Long id) {
		return methodCache.get(id);
	}

	public static boolean checkMethodThrow(Long id, Exception e) {
		Set<Class<?>> exceptionTypes = methodExceptionTypesCache.get(id);
		if (CollectionUtil.isEmpty(exceptionTypes)) {
			return false;
		}
		return exceptionTypes.contains(e.getClass()) || e instanceof RuntimeException;
	}
}
