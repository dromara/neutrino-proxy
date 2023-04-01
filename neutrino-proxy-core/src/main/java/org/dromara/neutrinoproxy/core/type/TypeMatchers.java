package org.dromara.neutrinoproxy.core.type;

import cn.hutool.core.collection.CollectionUtil;
import org.dromara.neutrinoproxy.core.type.extension.*;
import org.dromara.neutrinoproxy.core.util.Assert;
import org.dromara.neutrinoproxy.core.type.extension.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
			throw new RuntimeException(String.format("内置扩展匹配器:[%s] 距离区间定义有误!", typeMatcherGroup.getClass().getSimpleName()));
		}
		for (TypeMatcherGroup group : extensionMatcherGroupList) {
			// 距离区间不能重叠
			if (!(typeMatcherGroup.getDistanceMin() > group.getDistanceMax() || typeMatcherGroup.getDistanceMax() < group.getDistanceMin())) {
				throw new RuntimeException(String.format("内置扩展匹配器:[%s]与[%s] 距离区间定义有重叠!", typeMatcherGroup.getClass().getSimpleName(), group.getClass().getSimpleName()));
			}
		}
		extensionMatcherGroupList.add(typeMatcherGroup);
	}

	/**
	 * 注册默认的类型匹配器组
	 * @param typeMatcherGroup
	 */
	private static synchronized void registerDefaultTypeMatcher(TypeMatcherGroup typeMatcherGroup) {
		if (null == typeMatcherGroup || CollectionUtil.isEmpty(typeMatcherGroup.matchers())) {
			return;
		}
		// 自定义匹配器距离区间检测
		if (typeMatcherGroup.getDistanceMin() < TypeMatchLevel.NULL.getDistanceMin() ||
			typeMatcherGroup.getDistanceMax() > TypeMatchLevel.SUPER.getDistanceMax() ||
			typeMatcherGroup.getDistanceMax() < typeMatcherGroup.getDistanceMin()) {
			throw new RuntimeException(String.format("默认匹配器组器:[%s] 距离区间定义有误!", typeMatcherGroup.getClass().getSimpleName()));
		}
		defaultTypeMatcherList.addAll(typeMatcherGroup.matchers());
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
			throw new RuntimeException(String.format("自定义扩展匹配器:[%s] 距离区间定义有误!", typeMatcherGroup.getClass().getSimpleName()));
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
		// 注册默认的类型匹配器组
		registerDefaultTypeMatcher(new DefaultTypeMatcherGroup(TypeMatchLevel.NULL.getDistanceMin(), TypeMatchLevel.SUPER.getDistanceMax()));

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
