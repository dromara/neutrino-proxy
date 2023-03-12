package fun.asgc.neutrino.proxy.core.type.extension;

import fun.asgc.neutrino.proxy.core.type.AbstractMatcherGroup;
import fun.asgc.neutrino.proxy.core.type.TypeMatchInfo;
import fun.asgc.neutrino.proxy.core.type.TypeMatcher;
import fun.asgc.neutrino.proxy.core.util.TypeUtil;

/**
 * 布尔值匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class BooleanMatcherGroup extends AbstractMatcherGroup {

	public BooleanMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isString(clazz) && TypeUtil.isBoolean(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter(((value, targetType) -> ((String)value).toLowerCase().equals("true")));
				}
				return matchInfo;
			}
		});
	}

}
