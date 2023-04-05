package org.dromara.neutrinoproxy.server.util;

import cn.hutool.core.util.StrUtil;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.base.rest.ServiceException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
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

    /**
     * check max length , if the string length exceeded ,an exception is thrown
     * if the string is empty , it's not checked
     * 检查最大长度,超出长度,则抛出异常 . 如果字符串本身为空,则不进行检查
     *
     * @param str       String
     * @param maxLength Maximum length
     * @param params Exception parameters
     * @throws {@link ServiceException}
     */
    public static void checkMaxLength(String str, int maxLength, Object... params) {
        if (!StrUtil.isEmpty(str) && maxLength < str.length()) {
            throw ServiceException.create(ExceptionConstant.FILED_LENGTH_OUT, params);
        }
    }

}
