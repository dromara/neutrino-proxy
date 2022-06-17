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
package fun.asgc.neutrino.core.util;

import fun.asgc.neutrino.core.type.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

/**
 *
 * @author: wen.y
 * @date: 2022/6/17
 */
public class TypeUtilTest {

	private static TypeMatchers typeMatchers = new TypeMatchers();

	static {
		typeMatchers.registerCustomTypeMatcher(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo typeMatchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (clazz == String.class && targetClass == Animal.class) {
					typeMatchInfo.setTypeDistance(TypeDistance.CUSTOM_MIN);
					typeMatchInfo.setTypeConverter(((value, targetType) -> new Animal().setName((String)value)));
				}
				return typeMatchInfo;
			}
		});
		typeMatchers.registerCustomTypeMatcher(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo typeMatchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (clazz == String.class && targetClass == Cat.class) {
					typeMatchInfo.setTypeDistance(TypeDistance.CUSTOM_MIN);
					typeMatchInfo.setTypeConverter((value, targetType) -> {
						String[] tmp = ((String)value).split(":");
						Cat cat = new Cat();
						if (tmp.length >= 1) {
							cat.setName(tmp[0]);
						}
						if (tmp.length >= 2) {
							cat.setAge(Integer.valueOf(tmp[1]));
						}
						return cat;
					});
				}
				return typeMatchInfo;
			}
		});
	}

	@Test
	public void conversionNull() {
		System.out.println(typeMatchers.conversion(null, boolean.class));
		System.out.println(typeMatchers.conversion(null, byte.class));
		System.out.println(typeMatchers.conversion(null, short.class));
		System.out.println(typeMatchers.conversion(null, char.class));
		System.out.println(typeMatchers.conversion(null, int.class));
		System.out.println(typeMatchers.conversion(null, long.class));
		System.out.println(typeMatchers.conversion(null, float.class));
		System.out.println(typeMatchers.conversion(null, double.class));
		System.out.println(typeMatchers.conversion(null, Boolean.class));
		System.out.println(typeMatchers.conversion(null, Byte.class));
		System.out.println(typeMatchers.conversion(null, Short.class));
		System.out.println(typeMatchers.conversion(null, Character.class));
		System.out.println(typeMatchers.conversion(null, Integer.class));
		System.out.println(typeMatchers.conversion(null, Long.class));
		System.out.println(typeMatchers.conversion(null, Float.class));
		System.out.println(typeMatchers.conversion(null, Double.class));
		System.out.println(typeMatchers.conversion(null, String.class));

		System.out.println(typeMatchers.conversion(null, Animal.class));
		System.out.println(typeMatchers.conversion(null, Cat.class));
	}

	@Test
	public void conversionString() {
		System.out.println(typeMatchers.conversion("aabb", boolean.class));
		System.out.println(typeMatchers.conversion("aabb", byte.class));
		System.out.println(typeMatchers.conversion("aabb", short.class));
		System.out.println(typeMatchers.conversion("aabb", char.class));
		System.out.println(typeMatchers.conversion("aabb", int.class));
		System.out.println(typeMatchers.conversion("aabb", long.class));
		System.out.println(typeMatchers.conversion("aabb", float.class));
		System.out.println(typeMatchers.conversion("aabb", double.class));
		System.out.println(typeMatchers.conversion("aabb", Boolean.class));
		System.out.println(typeMatchers.conversion("aabb", Byte.class));
		System.out.println(typeMatchers.conversion("aabb", Short.class));
		System.out.println(typeMatchers.conversion("aabb", Character.class));
		System.out.println(typeMatchers.conversion("aabb", Integer.class));
		System.out.println(typeMatchers.conversion("aabb", Long.class));
		System.out.println(typeMatchers.conversion("aabb", Float.class));
		System.out.println(typeMatchers.conversion("aabb", Double.class));
		System.out.println(typeMatchers.conversion("aabb", String.class));

		System.out.println(typeMatchers.conversion("aabb", Animal.class));
		System.out.println(typeMatchers.conversion("aabb:10", Cat.class));
	}

	@Test
	public void conversionInt() {
		typeMatchers.setEnableExtensionMatcher(false);

		System.out.println(typeMatchers.conversion(100, boolean.class));
		System.out.println(typeMatchers.conversion(101, byte.class));
		System.out.println(typeMatchers.conversion(102, short.class));
		System.out.println(typeMatchers.conversion(103, char.class));
		System.out.println(typeMatchers.conversion(104, int.class));
		System.out.println(typeMatchers.conversion(105, long.class));
		System.out.println(typeMatchers.conversion(106, float.class));
		System.out.println(typeMatchers.conversion(107, double.class));
		System.out.println(typeMatchers.conversion(108, Boolean.class));
		System.out.println(typeMatchers.conversion(109, Byte.class));
		System.out.println(typeMatchers.conversion(110, Short.class));
		System.out.println(typeMatchers.conversion(111, Character.class));
		System.out.println(typeMatchers.conversion(112, Integer.class));
		System.out.println(typeMatchers.conversion(113, Long.class));
		System.out.println(typeMatchers.conversion(114, Float.class));
		System.out.println(typeMatchers.conversion(115, Double.class));
		System.out.println(typeMatchers.conversion(116, String.class));

		System.out.println(typeMatchers.conversion(117, Animal.class));
		System.out.println(typeMatchers.conversion(118, Cat.class));
	}


	@Accessors(chain = true)
	@Data
	public static class Animal {
		private String name;
	}

	@Accessors(chain = true)
	@Data
	private static class Cat extends Animal {
		private int age;
	}
}
