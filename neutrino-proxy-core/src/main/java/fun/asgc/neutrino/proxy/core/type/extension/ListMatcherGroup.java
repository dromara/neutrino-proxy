package fun.asgc.neutrino.proxy.core.type.extension;

import fun.asgc.neutrino.proxy.core.type.AbstractMatcherGroup;
import fun.asgc.neutrino.proxy.core.type.TypeMatchInfo;
import fun.asgc.neutrino.proxy.core.type.TypeMatcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * list匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class ListMatcherGroup extends AbstractMatcherGroup {

	public ListMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (List.class == targetClass && clazz.isArray()) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter(((value, targetType) -> Stream.of((Object[])value).collect(Collectors.toList())));
				}
				return matchInfo;
			}
		});
	}

}
