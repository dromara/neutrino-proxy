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

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.type.TypeMatchInfo;
import fun.asgc.neutrino.core.type.TypeMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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
	public static final Map<Class<?>, Class<?>> basicTypeMap = new HashMap<Class<?>, Class<?>>() {
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
	public static final Map<Class<?>, Class<?>> wrapTypeMap = new HashMap<Class<?>, Class<?>>() {
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
		boolean.class, byte.class, short.class, char.class, int.class, long.class, float.class, double.class,
		Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class
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
		Class<?> clazz = getFieldType(field);
		return clazz == byte.class || clazz == Byte.class;
	}

	/**
	 * 是否是short类型
	 * @param field
	 * @return
	 */
	public static boolean isShort(Field field) {
		Class<?> clazz = getFieldType(field);
		return clazz == short.class || clazz == Short.class;
	}

	/**
	 * 是否是integer类型
	 * @param field
	 * @return
	 */
	public static boolean isInteger(Field field) {
		Class<?> clazz = getFieldType(field);
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
		Class<?> clazz = getFieldType(field);
		return clazz == float.class || clazz == Float.class;
	}

	/**
	 * 是否是double类型
	 * @param field
	 * @return
	 */
	public static boolean isDouble(Field field) {
		Class<?> clazz = getFieldType(field);
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
		Class<?> clazz = getFieldType(field);
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
		return clazz == java.util.Date.class || clazz == java.sql.Date.class;
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
		while (CollectionUtil.notEmpty(list) && list.get(0).getFirst() != superClass && list.get(0).getSecond() < 100) {
			Pair<Class<?>, Integer> current = list.remove(0);
			Class<?> clazz = current.getFirst();
			int level = current.getSecond();
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
		return Math.min(list.get(0).getSecond(), 100);
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

//		// 1、完全匹配
//		if (fieldType == valueType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.PERFECT);
//		}
//		// 2、包装匹配
//		if (isStrictBasicType(valueType) && getWrapType(valueType) == fieldType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.PACKING);
//		}
//		// 3、解包装匹配
//		if (isStrictBasicType(fieldType) && getWrapType(fieldType) == valueType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.UNPACKING);
//		}
//		// 4、类型提升
//		if (isNormalBasicType(fieldType) && isNormalBasicType(valueType)) {
//			int index1 = basicTypeList.indexOf(fieldType);
//			int index2 = basicTypeList.indexOf(valueType);
//			if (index1 > index2 && index2 > 0) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.ASCENSION_MIN + (index1 - index2) - 1);
//			}
//		}
//		// 5、超类匹配
//		if (fieldType.isAssignableFrom(valueType)) {
//			int inheritLevel = getInheritLevel(fieldType, valueType);
//			if (-1 == inheritLevel) {
//				throw new RuntimeException(String.format("超类匹配异常! fieldType:%s valueType:%s", fieldType.getName(), valueType.getName()));
//			}
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.SUPER_MIN + inheritLevel - 1);
//		}
//		// 6、自定义转换
//		if (fieldType == Date.class || fieldType == java.sql.Date.class) {
//			if (valueType == long.class) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN);
//			} else if (valueType == long.class) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 1);
//			}
//		}
//		if (fieldType == Long.class || fieldType == long.class) {
//			if (valueType == Date.class) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 1000);
//			} else if (valueType == java.sql.Date.class) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 1001);
//			}
//		}
//		if (fieldType == String.class) {
//			if (valueType == char.class) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 2000);
//			} else if (valueType == Character.class) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 2001);
//			} else if (Number.class.isAssignableFrom(valueType)) {
//				return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 2002);
//			}
//		}
//		if (Iterable.class.isAssignableFrom(fieldType) && Iterable.class.isAssignableFrom(valueType)) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 3000);
//		}
//		if (Iterable.class.isAssignableFrom(fieldType) && String[].class == valueType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 4000);
//		}
//		if (Iterable.class.isAssignableFrom(valueType) && String[].class == fieldType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 5000);
//		}
//		if (Iterable.class.isAssignableFrom(fieldType) && Integer[].class == valueType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 6000);
//		}
//		if (Iterable.class.isAssignableFrom(valueType) && Integer[].class == fieldType) {
//			return new TypeMatchInfo(fieldType, valueType, TypeDistance.CUSTOM_MIN + 7000);
//		}

		return new TypeMatchInfo(fieldType, valueType, null);
	}

	/**
	 * 类型提升
	 * @param value
	 * @param clazz
	 * @return
	 */
	public static Object ascensionType(Object value, Class<?> clazz) {
		Class<?> valueType = value.getClass();
		int index1 = basicTypeList.indexOf(valueType);
		int index2 = basicTypeList.indexOf(clazz);
		if (-1 == index1 || -1 == index2 || index1 >= index2) {
			return value;
		}
		if (getWrapType(valueType) == clazz || getWrapType(clazz) == valueType) {
			return value;
		}
		if (isChar(clazz)) {
			return value;
		}
		String strValue;
		if (isBoolean(valueType)) {
			strValue = ((Boolean)value) ? "0" : "1";
		} else if (isChar(valueType)){
			strValue = (int)((Character)value) + "";
		} else {
			strValue = String.valueOf(value);
		}
		if (isBoolean(clazz)) {
			return "0".equals(strValue);
		}
		Method method = ReflectUtil.getValueOfMethod(clazz, String.class);
		if (null == method) {
			return value;
		}

		try {
			return method.invoke(null, strValue);
		} catch (Exception e) {
			// ignore
		}
		return value;
	}

	/**
	 * 类型转换
	 * @param value
	 * @param targetType
	 * @return
	 */
	public static Object conversion(Object value, Class<?> targetType) {
		return defaultTypeMatchers.conversion(value, targetType);
	}

//	/**
//	 * 类型转换
//	 * @param value
//	 * @param targetType
//	 * @return
//	 */
//	public static Object conversion(Object value, Class<?> targetType) {
//		Assert.notNull(targetType, "目标类型不能为空！");
//		if (null == value) {
//			return null;
//		}
//		Class<?> valueType = value.getClass();
//		TypeMatchInfo matchingInfo = typeMatch(targetType, valueType);
//		if (matchingInfo.isNotMatch()) {
//			return null;
//		}
//		// 类型提升
//		if (matchingInfo.getTypeDistance() <= TypeDistance.ASCENSION_MAX) {
//			return TypeUtil.ascensionType(value, targetType);
//		}
//		// 超类
//		if (matchingInfo.getTypeDistance() <= TypeDistance.SUPER_MAX) {
//			return value;
//		}
//		if (matchingInfo.getTypeDistance() >= TypeDistance.CUSTOM_MIN && matchingInfo.getTypeDistance() <= TypeDistance.CUSTOM_MAX) {
//			// 自定义转换
//			if (targetType == Date.class) {
//				if (valueType == long.class || value == Long.class) {
//					return new Date((Long) value);
//				}
//			} else if (targetType == java.sql.Date.class) {
//				if (valueType == long.class || value == Long.class) {
//					return new java.sql.Date((Long) value);
//				}
//			} else if (targetType == Long.class || targetType == long.class) {
//				if (valueType == java.sql.Date.class || valueType == Date.class) {
//					return ((Date) value).getTime();
//				}
//			} else if (targetType == String.class) {
//				if (valueType == char.class || valueType == Character.class || Number.class.isAssignableFrom(valueType)) {
//					return String.valueOf(value);
//				}
//			} else if (Iterable.class.isAssignableFrom(targetType) && Iterable.class.isAssignableFrom(valueType)) {
//				if (Set.class.isAssignableFrom(targetType)) {
//					return Sets.newHashSet((Iterable) value);
//				}
//			} else if (Iterable.class.isAssignableFrom(valueType) && String[].class == targetType) {
//				if (List.class.isAssignableFrom(valueType)) {
//					return ((List<Object>) value).stream().map(String::valueOf).collect(Collectors.toList()).toArray(new String[]{});
//				}
//			} else if (Iterable.class.isAssignableFrom(valueType) && Integer[].class == targetType) {
//				if (List.class.isAssignableFrom(valueType)) {
//					return ((List<Object>) value).stream().map(String::valueOf).map(Integer::valueOf).collect(Collectors.toList()).toArray(new Integer[]{});
//				}
//			}
//		}
//		return null;
//	}
}
