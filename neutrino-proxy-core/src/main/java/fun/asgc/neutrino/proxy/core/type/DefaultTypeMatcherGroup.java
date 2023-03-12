package fun.asgc.neutrino.proxy.core.type;

import fun.asgc.neutrino.proxy.core.util.TypeUtil;

/**
 * 默认的类型匹配器组
 * @author: aoshiguchen
 * @date: 2022/7/1
 */
public class DefaultTypeMatcherGroup extends AbstractMatcherGroup {

	public DefaultTypeMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	protected void init() {
		// 空匹配
		add(new TypeMatcher() {
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
		add(new TypeMatcher() {
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
		add(new TypeMatcher() {
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
		add(new TypeMatcher() {
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
		add(new TypeMatcher() {
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
		add(new TypeMatcher() {
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
	}
}
