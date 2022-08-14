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
package fun.asgc.neutrino.proxy.server.util;

import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.server.base.rest.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/1
 */
public class ParamCheckUtil {

	public static void checkNotNull(Object obj, String name) {
		if (null == obj) {
			throw ServiceException.create(ExceptionConstant.PARAMS_NOT_NULL, name);
		}
	}

	public static void checkNotEmpty(String str, String name) {
		if (StringUtil.isEmpty(str)) {
			throw ServiceException.create(ExceptionConstant.PARAMS_NOT_EMPTY, name);
		}
	}

	public static void checkNotEmpty(Collection collection, String name) {
		if (null == collection || collection.isEmpty()) {
			throw ServiceException.create(ExceptionConstant.PARAMS_NOT_EMPTY, name);
		}
	}

	public static void checkNotEmpty(Map map, String name) {
		if (null == map || map.isEmpty()) {
			throw ServiceException.create(ExceptionConstant.PARAMS_NOT_EMPTY, name);
		}
	}

	public static void checkNotEmpty(Set set, String name) {
		if (null == set || set.isEmpty()) {
			throw ServiceException.create(ExceptionConstant.PARAMS_NOT_EMPTY, name);
		}
	}

	public static void checkExpression(boolean expression, ExceptionConstant constant, Object... params) {
		if (!expression) {
			throw ServiceException.create(constant, params);
		}
	}
}
