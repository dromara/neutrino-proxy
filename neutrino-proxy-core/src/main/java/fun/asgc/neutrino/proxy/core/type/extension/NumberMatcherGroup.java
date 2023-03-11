package fun.asgc.neutrino.proxy.core.type.extension;

import fun.asgc.neutrino.proxy.core.type.AbstractMatcherGroup;
import fun.asgc.neutrino.proxy.core.type.TypeMatchInfo;
import fun.asgc.neutrino.proxy.core.type.TypeMatcher;
import fun.asgc.neutrino.proxy.core.util.TypeUtil;

/**
 * 字符串匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class NumberMatcherGroup extends AbstractMatcherGroup {

	public NumberMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		// String -> Byte
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isByte(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter((value, targetType) -> {
						try {
							return Byte.valueOf((String)value);
						} catch (Exception e) {
							// ignore
						}
						return 0;
					});
				}
				return matchInfo;
			}
		});
		// String -> Short
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isShort(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin() + 1);
					matchInfo.setTypeConverter((value, targetType) -> {
						try {
							return Short.valueOf((String)value);
						} catch (Exception e) {
							// ignore
						}
						return 0;
					});
				}
				return matchInfo;
			}
		});
		// String -> Integer
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isInteger(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin() + 2);
					matchInfo.setTypeConverter((value, targetType) -> {
						try {
							return Integer.valueOf((String)value);
						} catch (Exception e) {
							// ignore
						}
						return 0;
					});
				}
				return matchInfo;
			}
		});
		// String -> Long
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isLong(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin() + 3);
					matchInfo.setTypeConverter((value, targetType) -> {
						try {
							return Long.valueOf((String)value);
						} catch (Exception e) {
							// ignore
						}
						return 0;
					});
				}
				return matchInfo;
			}
		});
		// String -> Float
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isFloat(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin() + 4);
					matchInfo.setTypeConverter((value, targetType) -> {
						try {
							return Float.valueOf((String)value);
						} catch (Exception e) {
							// ignore
						}
						return 0;
					});
				}
				return matchInfo;
			}
		});
		// String -> Double
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isDouble(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin() + 5);
					matchInfo.setTypeConverter((value, targetType) -> {
						try {
							return Double.valueOf((String)value);
						} catch (Exception e) {
							// ignore
						}
						return 0;
					});
				}
				return matchInfo;
			}
		});
		// date -> Long
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isLong(targetClass) && TypeUtil.isDate(clazz)) {
					matchInfo.setTypeDistance(getDistanceMin() + 6);
					matchInfo.setTypeConverter(((value, targetType) -> ((java.util.Date)value).getTime()));
				}
				return matchInfo;
			}
		});
		// Long - > int
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isInteger(targetClass) && TypeUtil.isLong(clazz)) {
					matchInfo.setTypeDistance(getDistanceMin() + 7);
					matchInfo.setTypeConverter(((value, targetType) -> ((Long)value).intValue()));
				}
				return matchInfo;
			}
		});
	}

}
