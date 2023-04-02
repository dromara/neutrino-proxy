package org.dromara.neutrinoproxy.core.type.extension;

import org.dromara.neutrinoproxy.core.type.AbstractMatcherGroup;
import org.dromara.neutrinoproxy.core.type.TypeMatchInfo;
import org.dromara.neutrinoproxy.core.type.TypeMatcher;
import org.dromara.neutrinoproxy.core.util.TypeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class DateMatcherGroup extends AbstractMatcherGroup {

	public DateMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		// Long -> Date
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isLong(clazz) && TypeUtil.isDate(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter(((value, targetType) -> {
						if (targetClass == Date.class) {
							return new Date((long)value);
						} else {
							return new java.sql.Date((long)value);
						}
					}));
				}
				return matchInfo;
			}
		});
		// LocalDateTime -> Date
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (LocalDateTime.class.isAssignableFrom(clazz) && TypeUtil.isDate(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin() + 1);
					matchInfo.setTypeConverter(((value, targetType) -> {
						ZoneId zone = ZoneId.systemDefault();
						Instant instant = ((LocalDateTime)value).atZone(zone).toInstant();
						return Date.from(instant);
					}));
				}
				return matchInfo;
			}
		});
	}

}
