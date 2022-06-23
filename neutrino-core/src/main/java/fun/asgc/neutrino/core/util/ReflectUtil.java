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

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.type.TypeMatchLevel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class ReflectUtil {

	private static Cache<Class<?>, Set<Field>> fieldsCache = new MemoryCache<>();
	private static Cache<Class<?>, Set<Field>> declaredFieldsCache = new MemoryCache<>();
	private static Cache<Class<?>, Set<Field>> inheritChainDeclaredFieldSetCache = new MemoryCache<>();
	private static Cache<Class<?>, Set<Method>> methodsCache = new MemoryCache<>();
	private static Cache<Class<?>, Set<Method>> declaredMethodsCache = new MemoryCache<>();
	private static Cache<Field,Method> getMethodCache = new MemoryCache<>();
	private static Cache<Field,Method> setMethodCache = new MemoryCache<>();

	/**
	 * 获取字段列表
	 * @param clazz
	 * @return
	 */
	public static Set<Field> getFields(Class<?> clazz) {
		return kvProcess(fieldsCache, clazz, c -> {
			Field[] fields = c.getFields();
			Set<Field> fieldSet = new HashSet<>();
			if (ArrayUtil.notEmpty(fields)) {
				fieldSet = Stream.of(fields).collect(Collectors.toSet());
			}
			return fieldSet;
		});
	}

	/**
	 * 获取字段列表
	 * @param clazz
	 * @return
	 */
	public static Set<Field> getDeclaredFields(Class<?> clazz) {
		return kvProcess(declaredFieldsCache, clazz, c -> {
			Field[] fields = c.getDeclaredFields();
			Set<Field> fieldSet = new HashSet<>();
			if (ArrayUtil.notEmpty(fields)) {
				fieldSet = Stream.of(fields).collect(Collectors.toSet());
			}
			return fieldSet;
		});
	}

	/**
	 * 获取继承链字段列表
	 * @param clazz
	 * @return
	 */
	public static Set<Field> getInheritChainDeclaredFieldSet(Class<?> clazz) {
		return getInheritChainDeclaredFieldSet(clazz, Sets.newHashSet(Object.class));
	}

	/**
	 * 获取继承链字段列表
	 * @param clazz
	 * @return
	 */
	public static Set<Field> getInheritChainDeclaredFieldSet(Class<?> clazz, Set<Class<?>> ignoreClasses) {
		return kvProcess(inheritChainDeclaredFieldSetCache,
			clazz,
			(Class<?> c) -> {
				Set<String> nameSet = new HashSet<>();
				Set<Field> set = new HashSet<>();
				Set<Class<?>> ignores = null == ignoreClasses ? new HashSet<>() : ignoreClasses;
				while (null != c && !ignores.contains(c)) {
					Set<Field> fields = getDeclaredFields(c);
					if (CollectionUtil.notEmpty(fields)) {
						for (Field field : fields) {
							if (field.getName().equals("this$0") || nameSet.contains(field.getName())) {
								continue;
							}
							nameSet.add(field.getName());
							set.add(field);
						}
					}

					c = c.getSuperclass();
				}
				return set;
			}
		);
	}

	/**
	 * 获取方法列表
	 * @param clazz
	 * @return
	 */
	public static Set<Method> getMethods(Class<?> clazz) {
		return kvProcess(methodsCache, clazz, c -> {
			Method[] methods = c.getMethods();
			Set<Method> methodSet = new HashSet<>();
			if (ArrayUtil.notEmpty(methods)) {
				methodSet = Stream.of(methods).collect(Collectors.toSet());
			}
			return methodSet;
		});
	}

	/**
	 * 获取方法列表
	 * @param clazz
	 * @return
	 */
	public static Set<Method> getDeclaredMethods(Class<?> clazz) {
		return kvProcess(declaredMethodsCache, clazz, c -> {
			Method[] methods = c.getDeclaredMethods();
			Set<Method> methodSet = new HashSet<>();
			if (ArrayUtil.notEmpty(methods)) {
				methodSet = Stream.of(methods).collect(Collectors.toSet());
			}
			return methodSet;
		});
	}

	/**
	 * kv处理逻辑封装
	 * @param cache
	 * @param k
	 * @param fn
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	private static <K,V> V kvProcess(Cache<K,V> cache, K k, Function<K,V>fn) {
		return LockUtil.doubleCheckProcess(
			() -> !cache.containsKey(k),
			k,
			() -> cache.set(k, fn.apply(k)),
			() -> cache.get(k)
		);
	}

	/**
	 * 获取Get方法名称
	 * @param field
	 * @return
	 */
	private static String getGetMethodName(Field field) {
		String fieldName = field.getName();
		String methodName = fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1));
		return TypeUtil.isBoolean(field) ? "is" + methodName : "get" + methodName;
	}

	/**
	 * 获取Set方法名称
	 * @param field
	 * @return
	 */
	private static String getSetMethodName(Field field) {
		String fieldName = field.getName();
		return "set" + fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1));
	}

	/**
	 * 获取Get方法
	 * @param field
	 * @return
	 */
	public static Method getGetMethod(Field field) {
		return kvProcess(getMethodCache, field, f -> {
			String getMethodName = getGetMethodName(field);
			return getMethods(field.getDeclaringClass()).stream()
				.filter(method -> method.getName().equals(getMethodName) && method.getParameters().length == 0)
				.findFirst().get();
		});
	}

	/**
	 * 获取valueof方法
	 * @param clazz
	 * @param params
	 * @return
	 */
	public static Method getValueOfMethod(Class<?> clazz, Class<?> ...params) {

		try {
			return clazz.getMethod("valueOf", params);
		} catch (NoSuchMethodException e) {
			// ignore
		}
		return null;
	}

	/**
	 * 获取set方法
	 * @param field
	 * @return
	 */
	public static Method getSetMethod(Field field) {
		return kvProcess(setMethodCache, field, f -> {
			String setMethodName = getSetMethodName(field);
			Optional<Method> methodOptional = getMethods(field.getDeclaringClass()).stream()
				.filter(method -> method.getName().equals(setMethodName) && method.getParameters().length == 1 && method.getParameterTypes()[0] == field.getType())
				.findFirst();
			if (methodOptional.isPresent()) {
				return methodOptional.get();
			}
			int level = TypeMatchLevel.NOT.getDistanceMax();
			Method method = null;
			for (Method m : getMethods(field.getDeclaringClass())) {
				if (m.getName().equals(setMethodName) && m.getParameters().length == 1) {
					int curLevel = TypeUtil.typeMatch(m.getParameterTypes()[0], field.getType()).getTypeDistance();
					if (curLevel < level) {
						level = curLevel;
						method = m;
					}
				}
			}
			if (level < TypeMatchLevel.NOT.getDistanceMin() && method != null) {
				return method;
			}
			return null;
		});
	}

	/**
	 * 通过方法设置字段的值
	 * @param field
	 * @param obj
	 * @param value
	 */
	public static boolean setFieldValueByMethod(Field field, Object obj, Object value) {
		Assert.notNull(field, "field不能为空");
		Assert.notNull(obj, "对象不能为空");
		Assert.notNull(value, "值不能为空");

		try {
			Method setMethod = getSetMethod(field);
			if (null == setMethod) {
				return false;
			}
			if (null != value) {
				value = TypeUtil.conversion(value, field.getType());
				if (null == value) {
					return false;
				}
			}
			setMethod.invoke(obj, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置字段的值
	 * 先尝试通过set方法设置，若设置失败则通过字段直接设置
	 * @param field
	 * @param obj
	 * @param value
	 * @return
	 */
	public static boolean setFieldValue(Field field, Object obj, Object value) {
		Assert.notNull(field, "field不能为空");
		Assert.notNull(obj, "对象不能为空");

		try {
			return setFieldValueByMethod(field, obj , value) || setFieldValueByField(field, obj, value);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 通过字段设置值
	 * @param field
	 * @param obj
	 * @param value
	 * @return
	 */
	public static boolean setFieldValueByField(Field field, Object obj, Object value) {
		Assert.notNull(field, "field不能为空!");
		Assert.notNull(obj, "对象不能为空!");

		try {
			Class<?> fieldType = field.getType();

			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			if (null == value) {
				if (TypeUtil.isStrictBasicType(fieldType)) {
					return false;
				}
				field.set(obj, null);
				return true;
			}
			value = TypeUtil.conversion(value, fieldType);
			if (null != value) {
				field.set(obj, value);
				return true;
			}

		} catch (Exception e) {
			// ignore
		}
		return false;
	}
}
