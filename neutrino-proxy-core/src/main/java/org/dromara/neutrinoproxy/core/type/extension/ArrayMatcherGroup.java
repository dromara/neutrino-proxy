package org.dromara.neutrinoproxy.core.type.extension;

import org.dromara.neutrinoproxy.core.type.AbstractMatcherGroup;
import org.dromara.neutrinoproxy.core.type.TypeMatchInfo;
import org.dromara.neutrinoproxy.core.type.TypeMatcher;

import java.util.List;
import java.util.Set;

/**
 * 数组匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class ArrayMatcherGroup extends AbstractMatcherGroup {

	public ArrayMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		// List -> Object[]
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (List.class.isAssignableFrom(clazz) && targetClass.isArray()) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter((value, targetType) -> ((List)value).toArray());
				}
				return matchInfo;
			}
		});

		// Set -> Object[]
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (Set.class.isAssignableFrom(clazz) && targetClass.isArray()) {
					matchInfo.setTypeDistance(getDistanceMin() + 1);
					matchInfo.setTypeConverter((value, targetType) -> ((Set)value).toArray());
				}
				return matchInfo;
			}
		});
	}

}
