/**
 * Copyright (C) 2018-2022 Zeyi information technology (Shanghai) Co., Ltd.
 * <p>
 * All right reserved.
 * <p>
 * This software is the confidential and proprietary
 * information of Zeyi Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Zeyi inc.
 */
package fun.asgc.neutrino.core.type;

import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.TypeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author: wen.y
 * @date: 2022/6/17
 */
public class TypeMatchers {
	/**
	 * 默认的基本匹配器列表
	 */
	private static List<TypeMatcher> defaultTypeMatcherList = Collections.synchronizedList(new ArrayList<>());
	/**
	 * 默认内置扩展的匹配器列表
	 */
	private static List<TypeMatcher> extensionMatcherList = Collections.synchronizedList(new ArrayList<>());
	/**
	 * 用户自定义的匹配器列表
	 */
	private List<TypeMatcher> customTypeMatcherList = Collections.synchronizedList(new ArrayList<>());
	/**
	 * 是否启用内置扩展匹配器
	 */
	private volatile boolean enableExtensionMatcher = true;

	static {
		// 空匹配
		defaultTypeMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (null == clazz) {
					matchInfo.setTypeDistance(TypeMatchLevel.NULL.getDistanceMin());
					matchInfo.setTypeConverter((value, type) -> TypeUtil.getDefaultValue(type));
				}
				return matchInfo;
			}
		});
		// 完美匹配
		defaultTypeMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (clazz == targetClass) {
					matchInfo.setTypeDistance(TypeMatchLevel.PERFECT.getDistanceMin());
					matchInfo.setTypeConverter((value, type) -> value);
				}
				return matchInfo;
			}
		});
		// 装箱匹配
		defaultTypeMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isStrictBasicType(clazz) && TypeUtil.getWrapType(clazz) == targetClass) {
					matchInfo.setTypeDistance(TypeMatchLevel.PACKING.getDistanceMin());
					matchInfo.setTypeConverter((value, type) -> value);
				}
				return matchInfo;
			}
		});
		// 拆箱匹配
		defaultTypeMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isStrictBasicType(targetClass) && TypeUtil.getWrapType(targetClass) == clazz) {
					matchInfo.setTypeDistance(TypeMatchLevel.UNPACKING.getDistanceMin());
					matchInfo.setTypeConverter((value, type) -> value);
				}
				return matchInfo;
			}
		});
		// 类型提升匹配
		defaultTypeMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				int index1 = TypeUtil.basicTypeList.indexOf(clazz);
				int index2 = TypeUtil.basicTypeList.indexOf(targetClass);
				if (index2 > index1 && index1 >= 0) {
					matchInfo.setTypeDistance(TypeMatchLevel.ASCENSION.getDistanceMin() + (index2 - index1));
					matchInfo.setTypeConverter((value, type) -> {
						if (value == type || TypeUtil.getWrapType(value.getClass()) == type) {
							return value;
						}
						if (TypeUtil.isBoolean(value.getClass())) {
							return ((boolean)value) ? 1 : 0;
						}
						if (TypeUtil.isBoolean(type)) {
							return TypeUtil.isChar(value.getClass()) ? ((char)value == 0 ? false : true) :
								(((Number)value).intValue() == 0 ? false : true);
						}
						if (TypeUtil.isChar(type)) {
							return (char)(int)value;
						}
						return value;
					});
				}
				return matchInfo;
			}
		});
		// 超类匹配
		defaultTypeMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (targetClass.isAssignableFrom(clazz)) {
					int level = TypeUtil.getInheritLevel(targetClass, clazz);
					matchInfo.setTypeDistance(TypeMatchLevel.SUPER.getDistanceMin() + level);
					matchInfo.setTypeConverter((value, type) -> value);
				}
				return matchInfo;
			}
		});
		// 内置扩展匹配 --------
		// 字符串
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isNormalBasicType(clazz) && targetClass == String.class) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 10);
					matchInfo.setTypeConverter(((value, targetType) -> String.valueOf(value)));
				}
				return matchInfo;
			}
		});
		// 日期1
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isLong(clazz) && TypeUtil.isDate(targetClass)) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 20);
					matchInfo.setTypeConverter(((value, targetType) -> {
						if (targetClass == java.util.Date.class) {
							return new java.util.Date((long)value);
						} else {
							return new java.sql.Date((long)value);
						}
					}));
				}
				return matchInfo;
			}
		});
		// 日期2
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isLong(targetClass) && TypeUtil.isDate(clazz)) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 30);
					matchInfo.setTypeConverter(((value, targetType) -> ((java.util.Date)value).getTime()));
				}
				return matchInfo;
			}
		});
		// Object[] -> List
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (List.class == targetClass && clazz.isArray()) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 40);
					matchInfo.setTypeConverter(((value, targetType) -> Stream.of((Object[])value).collect(Collectors.toList())));
				}
				return matchInfo;
			}
		});
		// List -> Object[]
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (List.class.isAssignableFrom(clazz) && targetClass.isArray()) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 50);
					matchInfo.setTypeConverter((value, targetType) -> ((List)value).toArray());
				}
				return matchInfo;
			}
		});
		// Object[] -> Set
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (Set.class == targetClass && clazz.isArray()) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 60);
					matchInfo.setTypeConverter(((value, targetType) -> Stream.of((Object[])value).collect(Collectors.toSet())));
				}
				return matchInfo;
			}
		});
		// Set -> Object[]
		extensionMatcherList.add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (Set.class.isAssignableFrom(clazz) && targetClass.isArray()) {
					matchInfo.setTypeDistance(TypeMatchLevel.EXTENSION.getDistanceMin() + 70);
					matchInfo.setTypeConverter((value, targetType) -> ((Set)value).toArray());
				}
				return matchInfo;
			}
		});

	}

	/**
	 * 注册自定义类型匹配器具
	 * @param typeMatcher
	 */
	public void registerCustomTypeMatcher(TypeMatcher typeMatcher) {
		customTypeMatcherList.add(typeMatcher);
	}

	/**
	 * 匹配
	 * @param clazz
	 * @param targetClass
	 * @return
	 */
	public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
		// 先匹配自定义的
		for (TypeMatcher matcher : customTypeMatcherList) {
			TypeMatchInfo typeMatchInfo = matcher.match(clazz, targetClass);
			if (typeMatchInfo.isMatched()) {
				return typeMatchInfo;
			}
		}
		// 再匹配内置默认的
		for (TypeMatcher matcher : defaultTypeMatcherList) {
			TypeMatchInfo typeMatchInfo = matcher.match(clazz, targetClass);
			if (typeMatchInfo.isMatched()) {
				return typeMatchInfo;
			}
		}
		if (enableExtensionMatcher) {
			// 再匹配内置扩展的
			for (TypeMatcher matcher : extensionMatcherList) {
				TypeMatchInfo typeMatchInfo = matcher.match(clazz, targetClass);
				if (typeMatchInfo.isMatched()) {
					return typeMatchInfo;
				}
			}
		}
		return null;
	}

	public boolean isEnableExtensionMatcher() {
		return enableExtensionMatcher;
	}

	public void setEnableExtensionMatcher(boolean enableExtensionMatcher) {
		this.enableExtensionMatcher = enableExtensionMatcher;
	}

	/**
	 * 类型转换
	 * @param value
	 * @param targetType
	 * @return
	 */
	public Object conversion(Object value, Class<?> targetType) {
		Assert.notNull(targetType, "目标类型不能为空！");
		TypeMatchInfo typeMatchInfo = match(value == null ? null : value.getClass(), targetType);
		if (null == typeMatchInfo || typeMatchInfo.isNotMatch()) {
			return null;
		}
		return typeMatchInfo.getTypeConverter().convert(value, targetType);
	}
}
