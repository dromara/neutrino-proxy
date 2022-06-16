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

package fun.asgc.neutrino.core.util;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public abstract class Assert {

	/**
	 *
	 * @param expression
	 * @param message
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 *
	 * @param expression
	 * @param messageSupplier
	 */
	public static void state(boolean expression, Supplier<String> messageSupplier) {
		if (!expression) {
			throw new IllegalStateException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param expression
	 * @param message
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param expression
	 * @param messageSupplier
	 */
	public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
		if (!expression) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param object
	 * @param message
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param object
	 * @param messageSupplier
	 */
	public static void isNull(Object object, Supplier<String> messageSupplier) {
		if (object != null) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param object
	 * @param message
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param object
	 * @param messageSupplier
	 */
	public static void notNull(Object object, Supplier<String> messageSupplier) {
		if (object == null) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param text
	 * @param message
	 */
	public static void hasLength(String text, String message) {
		if (!StringUtil.hasLength(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param text
	 * @param messageSupplier
	 */
	public static void hasLength(String text, Supplier<String> messageSupplier) {
		if (!StringUtil.hasLength(text)) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param text
	 * @param message
	 */
	public static void hasText(String text, String message) {
		if (!StringUtil.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param text
	 * @param messageSupplier
	 */
	public static void hasText(String text, Supplier<String> messageSupplier) {
		if (!StringUtil.hasText(text)) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param textToSearch
	 * @param substring
	 * @param message
	 */
	public static void doesNotContain(String textToSearch, String substring, String message) {
		if (StringUtil.hasLength(textToSearch) && StringUtil.hasLength(substring) &&
			textToSearch.contains(substring)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param textToSearch
	 * @param substring
	 * @param messageSupplier
	 */
	public static void doesNotContain(String textToSearch, String substring, Supplier<String> messageSupplier) {
		if (StringUtil.hasLength(textToSearch) && StringUtil.hasLength(substring) &&
			textToSearch.contains(substring)) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param array
	 * @param message
	 */
	public static void notEmpty(Object[] array, String message) {
		if (ObjectUtils.isEmpty(array)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param array
	 * @param messageSupplier
	 */
	public static void notEmpty(Object[] array, Supplier<String> messageSupplier) {
		if (ObjectUtils.isEmpty(array)) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param array
	 * @param message
	 */
	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	/**
	 *
	 * @param array
	 * @param messageSupplier
	 */
	public static void noNullElements(Object[] array, Supplier<String> messageSupplier) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(nullSafeGet(messageSupplier));
				}
			}
		}
	}

	/**
	 *
	 * @param collection
	 * @param message
	 */
	public static void notEmpty(Collection<?> collection, String message) {
		if (CollectionUtil.isEmpty(collection)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param collection
	 * @param messageSupplier
	 */
	public static void notEmpty(Collection<?> collection, Supplier<String> messageSupplier) {
		if (CollectionUtil.isEmpty(collection)) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param map
	 * @param message
	 */
	public static void notEmpty(Map<?, ?> map, String message) {
		if (CollectionUtil.isEmpty(map)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 *
	 * @param map
	 * @param messageSupplier
	 */
	public static void notEmpty(Map<?, ?> map, Supplier<String> messageSupplier) {
		if (CollectionUtil.isEmpty(map)) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param type
	 * @param obj
	 * @param message
	 */
	public static void isInstanceOf(Class<?> type, Object obj, String message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			instanceCheckFailed(type, obj, message);
		}
	}

	/**
	 *
	 * @param type
	 * @param obj
	 * @param messageSupplier
	 */
	public static void isInstanceOf(Class<?> type, Object obj, Supplier<String> messageSupplier) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			instanceCheckFailed(type, obj, nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param type
	 * @param obj
	 */
	public static void isInstanceOf(Class<?> type, Object obj) {
		isInstanceOf(type, obj, "");
	}

	/**
	 *
	 * @param superType
	 * @param subType
	 * @param message
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
		notNull(superType, "Super type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			assignableCheckFailed(superType, subType, message);
		}
	}

	/**
	 *
	 * @param superType
	 * @param subType
	 * @param messageSupplier
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType, Supplier<String> messageSupplier) {
		notNull(superType, "Super type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			assignableCheckFailed(superType, subType, nullSafeGet(messageSupplier));
		}
	}

	/**
	 *
	 * @param superType
	 * @param subType
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType) {
		isAssignable(superType, subType, "");
	}


	private static void instanceCheckFailed(Class<?> type, Object obj, String msg) {
		String className = (obj != null ? obj.getClass().getName() : "null");
		String result = "";
		boolean defaultMessage = true;
		if (StringUtil.hasLength(msg)) {
			if (endsWithSeparator(msg)) {
				result = msg + " ";
			}
			else {
				result = messageWithTypeName(msg, className);
				defaultMessage = false;
			}
		}
		if (defaultMessage) {
			result = result + ("Object of class [" + className + "] must be an instance of " + type);
		}
		throw new IllegalArgumentException(result);
	}

	private static void assignableCheckFailed(Class<?> superType, Class<?> subType, String msg) {
		String result = "";
		boolean defaultMessage = true;
		if (StringUtil.hasLength(msg)) {
			if (endsWithSeparator(msg)) {
				result = msg + " ";
			}
			else {
				result = messageWithTypeName(msg, subType);
				defaultMessage = false;
			}
		}
		if (defaultMessage) {
			result = result + (subType + " is not assignable to " + superType);
		}
		throw new IllegalArgumentException(result);
	}

	private static boolean endsWithSeparator(String msg) {
		return (msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith("."));
	}

	private static String messageWithTypeName(String msg, Object typeName) {
		return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
	}

	private static String nullSafeGet(Supplier<String> messageSupplier) {
		return (messageSupplier != null ? messageSupplier.get() : null);
	}

}
