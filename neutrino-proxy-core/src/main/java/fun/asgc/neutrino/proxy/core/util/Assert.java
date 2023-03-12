package fun.asgc.neutrino.proxy.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
	 * @param array
	 * @param message
	 */
	public static void notEmpty(Object[] array, String message) {
		if (ObjectUtils.isEmpty(array)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(String s, String message) {
		if (StrUtil.isEmpty(s)) {
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
