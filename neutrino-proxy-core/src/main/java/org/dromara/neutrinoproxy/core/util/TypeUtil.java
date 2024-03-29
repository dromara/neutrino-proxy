package org.dromara.neutrinoproxy.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import com.google.common.collect.Lists;
import org.dromara.neutrinoproxy.core.type.TypeMatchInfo;
import org.dromara.neutrinoproxy.core.type.TypeMatchers;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class TypeUtil {

	/**
	 * 严格意义上的基本数据类型
	 */
	private static final Set<Class<?>> strictBasicType = new HashSet<Class<?>>() {
		{
			this.add(byte.class);
			this.add(short.class);
			this.add(int.class);
			this.add(long.class);
			this.add(float.class);
			this.add(double.class);
			this.add(char.class);
			this.add(boolean.class);
		}
	};

	/**
	 * 一般意义上的基本数据类型
	 */
	private static final Set<Class<?>> normalBasicType = new HashSet<Class<?>>() {
		{
			this.addAll(strictBasicType);

			this.add(Byte.class);
			this.add(Short.class);
			this.add(Integer.class);
			this.add(Long.class);
			this.add(Float.class);
			this.add(Double.class);
			this.add(Character.class);
			this.add(Boolean.class);
			this.add(String.class);
		}
	};
	/**
	 * 基本类型的映射
	 */
	private static final Map<Class<?>, Class<?>> basicTypeMap = new HashMap<Class<?>, Class<?>>() {
		{
			this.put(byte.class, byte.class);
			this.put(short.class, short.class);
			this.put(int.class, int.class);
			this.put(long.class, long.class);
			this.put(float.class, float.class);
			this.put(double.class, double.class);
			this.put(char.class, char.class);
			this.put(boolean.class, boolean.class);

			this.put(Byte.class, byte.class);
			this.put(Short.class, short.class);
			this.put(Integer.class, int.class);
			this.put(Long.class, long.class);
			this.put(Float.class, float.class);
			this.put(Double.class, double.class);
			this.put(Character.class, char.class);
			this.put(Boolean.class, boolean.class);
		}
	};

	/**
	 * 包装类型的映射
	 */
	private static final Map<Class<?>, Class<?>> wrapTypeMap = new HashMap<Class<?>, Class<?>>() {
		{
			this.put(byte.class, Byte.class);
			this.put(short.class, Short.class);
			this.put(int.class, Integer.class);
			this.put(long.class, Long.class);
			this.put(float.class, Float.class);
			this.put(double.class, Double.class);
			this.put(char.class, Character.class);
			this.put(boolean.class, Boolean.class);

			this.put(Byte.class, Byte.class);
			this.put(Short.class, Short.class);
			this.put(Integer.class, Integer.class);
			this.put(Long.class, Long.class);
			this.put(Float.class, Float.class);
			this.put(Double.class, Double.class);
			this.put(Character.class, Character.class);
			this.put(Boolean.class, Boolean.class);

			this.put(String.class, String.class);
		}
	};
	/**
	 * 基本类型列表（用于计算类型提升距离）
	 */
	public static final List<Class<?>> basicTypeList = Lists.newArrayList(
		boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class, char.class, Character.class,
		int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class
	);

	/**
	 * 默认值
	 */
	private static final Map<Class<?>, Object> defaultValueMap = new HashMap<Class<?>, Object>() {
		{
			this.put(byte.class, 0);
			this.put(short.class, 0);
			this.put(int.class, 0);
			this.put(long.class, 0);
			this.put(float.class, 0);
			this.put(double.class, 0);
			this.put(char.class, 0);
			this.put(boolean.class, false);
		}
	};

	/**
	 * 默认的类型匹配器
	 */
	private static final TypeMatchers defaultTypeMatchers = new TypeMatchers();

	/**
	 * 获取字段类型
	 * @param field
	 * @return
	 */
	public static Class<?> getFieldType(Field field) {
		return field.getType();
	}

	/**
	 * 字段类型匹配
	 * @param field
	 * @param clazz
	 * @return
	 */
	public static boolean fieldTypeMatch(Field field, Class<?> clazz) {
		return getFieldType(field) == clazz;
	}

	/**
	 * 是否是严格基本类型
	 * @param clazz
	 * @return
	 */
	public static boolean isStrictBasicType(Class<?> clazz) {
		return strictBasicType.contains(clazz);
	}

	/**
	 * 是否是严格基本类型
	 * @param field
	 * @return
	 */
	public static boolean isStrictBasicType(Field field) {
		return isStrictBasicType(getFieldType(field));
	}

	/**
	 * 是否是一般基本类型
	 * @param clazz
	 * @return
	 */
	public static boolean isNormalBasicType(Class<?> clazz) {
		return normalBasicType.contains(clazz);
	}

	/**
	 * 是否是数字
	 * @param clazz
	 * @return
	 */
	public static boolean isNumber(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz);
	}

	/**
	 * 是否是一般基本类型
	 * @param field
	 * @return
	 */
	public static boolean isNormalBasicType(Field field) {
		return isNormalBasicType(getFieldType(field));
	}

	/**
	 * 是否是包装类型
	 * @param clazz
	 * @return
	 */
	public static boolean isWrapType(Class<?> clazz) {
		return !isStrictBasicType(clazz);
	}

	/**
	 * 是否是包装类型
	 * @param field
	 * @return
	 */
	public static boolean isWrapType(Field field) {
		return !isStrictBasicType(field);
	}

	/**
	 * 是否是byte类型
	 * @param field
	 * @return
	 */
	public static boolean isByte(Field field) {
		return isByte(getFieldType(field));
	}

	/**
	 * 是否是byte类型
	 * @param clazz
	 * @return
	 */
	public static boolean isByte(Class clazz) {
		return clazz == byte.class || clazz == Byte.class;
	}

	/**
	 * 是否是short类型
	 * @param field
	 * @return
	 */
	public static boolean isShort(Field field) {
		return isShort(getFieldType(field));
	}

	/**
	 * 是否是short类型
	 * @param clazz
	 * @return
	 */
	public static boolean isShort(Class clazz) {
		return clazz == short.class || clazz == Short.class;
	}

	/**
	 * 是否是integer类型
	 * @param field
	 * @return
	 */
	public static boolean isInteger(Field field) {
		return isInteger(getFieldType(field));
	}

	/**
	 * 是否是integer类型
	 * @param clazz
	 * @return
	 */
	public static boolean isInteger(Class clazz) {
		return clazz == int.class || clazz == Integer.class;
	}

	/**
	 * 是否是long类型
	 * @param field
	 * @return
	 */
	public static boolean isLong(Field field) {
		return isLong(getFieldType(field));
	}

	/**
	 * 是否是long类型
	 * @param clazz
	 * @return
	 */
	public static boolean isLong(Class<?> clazz) {
		return clazz == long.class || clazz == Long.class;
	}

	/**
	 * 是否是float类型
	 * @param field
	 * @return
	 */
	public static boolean isFloat(Field field) {
		return isFloat(getFieldType(field));
	}

	/**
	 * 是否是float类型
	 * @param clazz
	 * @return
	 */
	public static boolean isFloat(Class<?> clazz) {
		return clazz == float.class || clazz == Float.class;
	}

	/**
	 * 是否是double类型
	 * @param field
	 * @return
	 */
	public static boolean isDouble(Field field) {
		return isDouble(getFieldType(field));
	}

	/**
	 * 是否是double类型
	 * @param clazz
	 * @return
	 */
	public static boolean isDouble(Class<?> clazz) {
		return clazz == double.class || clazz == Double.class;
	}

	/**
	 * 是否是char类型
	 * @param field
	 * @return
	 */
	public static boolean isChar(Field field) {
		Class<?> clazz = getFieldType(field);
		return clazz == char.class || clazz == Character.class;
	}

	/**
	 * 是否是boolean类型
	 * @param field
	 * @return
	 */
	public static boolean isBoolean(Field field) {
		Class<?> clazz = getFieldType(field);
		return clazz == boolean.class || clazz == Boolean.class;
	}

	/**
	 * 是否是String类型
	 * @param field
	 * @return
	 */
	public static boolean isString(Field field) {
		return isString(getFieldType(field));
	}

	/**
	 * 是否是String类型
	 * @param clazz
	 * @return
	 */
	public static boolean isString(Class clazz) {
		return clazz == String.class;
	}

	/**
	 * 是否是日期类型
	 * @param field
	 * @return
	 */
	public static boolean isDate(Field field) {
		return isDate(getFieldType(field));
	}

	/**
	 * 是否是日期类型
	 * @param clazz
	 * @return
	 */
	public static boolean isDate(Class<?> clazz) {
		return clazz == Date.class || clazz == java.sql.Date.class;
	}

	/**
	 * 是否是布尔值
	 * @param clazz
	 * @return
	 */
	public static boolean isBoolean(Class<?> clazz) {
		return clazz == boolean.class || clazz == Boolean.class;
	}

	/**
	 * 是否是字符
	 * @param clazz
	 * @return
	 */
	public static boolean isChar(Class<?> clazz) {
		return clazz == char.class || clazz == Character.class;
	}

	/**
	 * 是否是map类型
	 * @param clazz
	 * @return
	 */
	public static boolean isMap(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	/**
	 * 获取包装类型
	 * @param clazz
	 * @return
	 */
	public static Class<?> getWrapType(Class<?> clazz) {
		if (isStrictBasicType(clazz)) {
			return wrapTypeMap.get(clazz);
		}
		return clazz;
	}

	/**
	 * 获取包装类型
	 * @param field
	 * @return
	 */
	public static Class<?> getWrapType(Field field) {
		return getWrapType(getFieldType(field));
	}

	/**
	 * 是否是超类
	 * @param superClass
	 * @param childClass
	 * @return
	 */
	public static boolean isSupper(Class<?> superClass, Class<?> childClass) {
		return superClass.isAssignableFrom(childClass);
	}

	/**
	 * 获取简单类名
	 * @param clazz
	 * @return
	 */
	public static String getSimpleName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	/**
	 * 获取包名
	 * @param clazz
	 * @return
	 */
	public static String getPackageName(Class<?> clazz) {
		return clazz.getPackage().getName();
	}

	/**
	 * 获取默认变量名
	 * @param clazz
	 * @return
	 */
	public static String getDefaultVariableName(Class<?> clazz) {
		String name = getSimpleName(clazz);
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	/**
	 * 获取默认值
	 * @param clazz
	 * @return
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		return defaultValueMap.get(clazz);
	}

	/**
	 * 是否是集合类型
	 * @param clazz
	 * @return
	 */
	public static boolean isCollection(Class<?> clazz) {
		return Collection.class.isAssignableFrom(clazz);
	}

	/**
	 * 是否可列举
	 * @param clazz
	 * @return
	 */
	public static boolean isListable(Class<?> clazz) {
		return clazz.isArray() || isCollection(clazz);
	}

	/**
	 * 是否是list
	 * @param clazz
	 * @return
	 */
	public static boolean isList(Class<?> clazz) {
		return List.class.isAssignableFrom(clazz);
	}

	/**
	 * list转成其他形式
	 * @param list
	 * @param targetType
	 * @return
	 */
	public static Object listTo(List list, Class<?> targetType) {
		if (null == list) {
			return null;
		}
		if (isList(targetType)) {
			return list;
		}
		// 数组
		if (targetType.isArray()) {
			return list.toArray();
		}
		// set
		if (Set.class.isAssignableFrom(targetType)) {
			return list.stream().collect(Collectors.toSet());
		}
		return null;
	}

	/**
	 * 获取继承层级
	 * 此处假定继承层级最多为100层，避免计算太过耗时
	 * @param superClass
	 * @param childClass
	 * @return
	 */
	public static int getInheritLevel(Class<?> superClass, Class<?> childClass) {
		if (!isSupper(superClass, childClass)) {
			return -1;
		}
		List<Pair<Class<?>, Integer>> list = new LinkedList<>();
		list.add(Pair.of(childClass, 0));
		while (CollectionUtil.isNotEmpty(list) && list.get(0).getKey() != superClass && list.get(0).getValue() < 100) {
			Pair<Class<?>, Integer> current = list.remove(0);
			Class<?> clazz = current.getKey();
			int level = current.getValue();
			if (null != clazz.getSuperclass()) {
				list.add(Pair.of(clazz.getSuperclass(), level + 1));
			}
			if (null != clazz.getInterfaces() && clazz.getInterfaces().length > 0) {
				for (Class<?> inter : clazz.getInterfaces()) {
					list.add(Pair.of(inter, level + 1));
				}
			}
		}
		if (list.isEmpty()) {
			return 100;
		}
		return Math.min(list.get(0).getValue(), 100);
	}

	/**
	 * 计算一个类型的值是否可以赋值给指定类型的匹配信息
	 * @param fieldType
	 * @param valueType
	 * @return
	 */
	public static TypeMatchInfo typeMatch(Class<?> fieldType, Class<?> valueType) {
		Assert.notNull(fieldType, "字段类型不能为空！");
		Assert.notNull(valueType, "值类型不能为空！");
		return new TypeMatchInfo(fieldType, valueType, null);
	}

	/**
	 * 类型转换
	 * @param value
	 * @param targetType
	 * @return
	 */
	public static <T> T conversion(Object value, Class<T> targetType) {
		return (T)defaultTypeMatchers.conversion(value, targetType);
	}
}
