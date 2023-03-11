package fun.asgc.neutrino.proxy.server.util;

import cn.hutool.core.util.StrUtil;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
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
		if (StrUtil.isEmpty(str)) {
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

	public static void checkMustNull(Object obj, ExceptionConstant constant, Object... params) {
		if (null != obj) {
			throw ServiceException.create(constant, params);
		}
	}


	public static void checkNotNull(Object obj, ExceptionConstant constant, Object... params) {
		if (null == obj) {
			throw ServiceException.create(constant, params);
		}
	}

	public static void checkNotEmpty(String str, ExceptionConstant constant, Object... params) {
		if (StrUtil.isEmpty(str)) {
			throw ServiceException.create(constant, params);
		}
	}

	public static void checkNotEmpty(Collection collection, ExceptionConstant constant, Object... params) {
		if (null == collection || collection.isEmpty()) {
			throw ServiceException.create(constant, params);
		}
	}

	public static void checkNotEmpty(Map map, ExceptionConstant constant, Object... params) {
		if (null == map || map.isEmpty()) {
			throw ServiceException.create(constant, params);
		}
	}

	public static void checkNotEmpty(Set set, ExceptionConstant constant, Object... params) {
		if (null == set || set.isEmpty()) {
			throw ServiceException.create(constant, params);
		}
	}

	public static void checkExpression(boolean expression, ExceptionConstant constant, Object... params) {
		if (!expression) {
			throw ServiceException.create(constant, params);
		}
	}
}
