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
public class StringMatcherGroup extends AbstractMatcherGroup {

	public StringMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		// 基本类型 -> String
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isNormalBasicType(clazz) && TypeUtil.isString(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter(((value, targetType) -> String.valueOf(value)));
				}
				return matchInfo;
			}
		});
		// String -> char
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isChar(targetClass) && TypeUtil.isString(clazz)) {
					matchInfo.setTypeDistance(getDistanceMin() + 1);
					matchInfo.setTypeConverter(((value, targetType) -> ((String)value).charAt(0)));
				}
				return matchInfo;
			}
		});
	}

}
