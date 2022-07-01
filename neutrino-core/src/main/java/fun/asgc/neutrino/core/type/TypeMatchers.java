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
package fun.asgc.neutrino.core.type;

import fun.asgc.neutrino.core.type.extension.*;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.TypeUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/17
 */
public class TypeMatchers {
	/**
	 * 默认的基本匹配器列表
	 */
	private static List<TypeMatcher> defaultTypeMatcherList = new ArrayList<>();
	/**
	 * 默认内置扩展的匹配器列表
	 */
	private static List<TypeMatcher> extensionMatcherList = new ArrayList<>();
	/**
	 * 内置扩展匹配器组列表
	 */
	private static List<TypeMatcherGroup> extensionMatcherGroupList = new ArrayList<>();
	/**
	 * 用户自定义的匹配器列表
	 */
	private List<TypeMatcher> customTypeMatcherList = new ArrayList<>();
	/**
	 * 是否启用内置扩展匹配器
	 */
	private volatile boolean enableExtensionMatcher = true;

	static {
		init();
	}

	/**
	 * 注册内置扩展匹配器组
	 * @param typeMatcherGroup
	 */
	private static synchronized void registerExtensionTypeMatcher(TypeMatcherGroup typeMatcherGroup) {
		if (null == typeMatcherGroup || CollectionUtil.isEmpty(typeMatcherGroup.matchers())) {
			return;
		}
		// 内置扩展匹配器距离区间检测
		if (typeMatcherGroup.getDistanceMin() < TypeMatchLevel.EXTENSION.getDistanceMin() ||
			typeMatcherGroup.getDistanceMax() > TypeMatchLevel.EXTENSION.getDistanceMax() ||
			typeMatcherGroup.getDistanceMax() < typeMatcherGroup.getDistanceMin()) {
			throw new RuntimeException(String.format("内置扩展器:[%s] 距离区间定义有误!", typeMatcherGroup.getClass().getSimpleName()));
		}
		for (TypeMatcherGroup group : extensionMatcherGroupList) {
			// 距离区间不能重叠
			if (!(typeMatcherGroup.getDistanceMin() > group.getDistanceMax() || typeMatcherGroup.getDistanceMax() < group.getDistanceMin())) {
				throw new RuntimeException(String.format("内置扩展器:[%s]与[%s] 距离区间定义有重叠!", typeMatcherGroup.getClass().getSimpleName(), group.getClass().getSimpleName()));
			}
		}
		extensionMatcherGroupList.add(typeMatcherGroup);
	}

	/**
	 * 注册自定义类型匹配器具
	 * @param typeMatcher
	 */
	public synchronized void registerCustomTypeMatcher(TypeMatcher typeMatcher) {
		Assert.notNull(typeMatcher, "匹配器不能为空!");
		customTypeMatcherList.add(typeMatcher);
	}

	/**
	 * 注册自定义匹配器组
	 * @param typeMatcherGroup
	 */
	public synchronized void registerCustomTypeMatcher(TypeMatcherGroup typeMatcherGroup) {
		if (null == typeMatcherGroup || CollectionUtil.isEmpty(typeMatcherGroup.matchers())) {
			return;
		}
		// 自定义匹配器距离区间检测
		if (typeMatcherGroup.getDistanceMin() < TypeMatchLevel.CUSTOM.getDistanceMin() ||
			typeMatcherGroup.getDistanceMax() > TypeMatchLevel.CUSTOM.getDistanceMax() ||
			typeMatcherGroup.getDistanceMax() < typeMatcherGroup.getDistanceMin()) {
			throw new RuntimeException(String.format("自定义扩展器:[%s] 距离区间定义有误!", typeMatcherGroup.getClass().getSimpleName()));
		}
		customTypeMatcherList.addAll(typeMatcherGroup.matchers());
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

	/**
	 * 初始化
	 */
	private static void init() {
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
						if (TypeUtil.isInteger(clazz) && TypeUtil.isLong(targetClass)) {
							return Long.valueOf(String.valueOf(value));
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

		// 注册内置扩展匹配
		int extensionMatcherGroupSize = 100000;
		int extensionMatcherGroupDistance = TypeMatchLevel.EXTENSION.getDistanceMin();

		registerExtensionTypeMatcher(new StringMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 0,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 1 - 1));
		registerExtensionTypeMatcher(new BooleanMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 1,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 2 - 1));
		registerExtensionTypeMatcher(new NumberMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 2,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 3 - 1));
		registerExtensionTypeMatcher(new DateMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 3,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 4 - 1));
		registerExtensionTypeMatcher(new ListMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 4,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 5 - 1));
		registerExtensionTypeMatcher(new SetMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 5,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 6 - 1));
		registerExtensionTypeMatcher(new MapMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 6,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 7 - 1));
		registerExtensionTypeMatcher(new ArrayMatcherGroup(extensionMatcherGroupDistance + extensionMatcherGroupSize * 7,
			extensionMatcherGroupDistance + extensionMatcherGroupSize * 8 - 1));

		// 按照距离区间排序
		extensionMatcherList = extensionMatcherGroupList.stream().sorted(Comparator.comparing(TypeMatcherGroup::getDistanceMin))
			.map(TypeMatcherGroup::matchers).flatMap(List::stream).collect(Collectors.toList());
	}
}
