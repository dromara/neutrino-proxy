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

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.type.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.List;
import java.util.Set;

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
					typeMatchInfo.setTypeDistance(TypeMatchLevel.CUSTOM.getDistanceMin() + 1);
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
					typeMatchInfo.setTypeDistance(TypeMatchLevel.CUSTOM.getDistanceMin() + 2);
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
		System.out.println(typeMatchers.conversion(null, List.class));

		System.out.println(typeMatchers.conversion(null, Animal.class));
		System.out.println(typeMatchers.conversion(null, Cat.class));
	}

	@Test
	public void conversionString() {
		System.out.println(typeMatchers.conversion("true", boolean.class));
		System.out.println(typeMatchers.conversion("100", byte.class));
		System.out.println(typeMatchers.conversion("101", short.class));
		System.out.println(typeMatchers.conversion("a", char.class));
		System.out.println(typeMatchers.conversion("1000", int.class));
		System.out.println(typeMatchers.conversion("500000", long.class));
		System.out.println(typeMatchers.conversion("1.3", float.class));
		System.out.println(typeMatchers.conversion("566.0099", double.class));
		System.out.println(typeMatchers.conversion("false", Boolean.class));
		System.out.println(typeMatchers.conversion("88", Byte.class));
		System.out.println(typeMatchers.conversion("444", Short.class));
		System.out.println(typeMatchers.conversion("B", Character.class));
		System.out.println(typeMatchers.conversion("99", Integer.class));
		System.out.println(typeMatchers.conversion("5666666", Long.class));
		System.out.println(typeMatchers.conversion("10.24", Float.class));
		System.out.println(typeMatchers.conversion("3.1415926", Double.class));
		System.out.println(typeMatchers.conversion('a', String.class));

		System.out.println(typeMatchers.conversion("aabb", Animal.class));
		System.out.println(typeMatchers.conversion("aabb:10", Cat.class));

		System.out.println(typeMatchers.conversion(new String[]{"11","22","33","22"}, List.class));
		System.out.println(typeMatchers.conversion(new Integer[]{10, 20, 30,20}, List.class));
		System.out.println(typeMatchers.conversion(new Boolean[]{true, true, false}, List.class));
		System.out.println(typeMatchers.conversion(new Animal[]{new Animal("aa"), new Animal("bb"), new Animal("cc"),}, List.class));

		System.out.println(typeMatchers.conversion(Lists.newArrayList("11","22","33","22"), Object[].class));

		System.out.println(typeMatchers.conversion(new String[]{"11","22","33","22"}, Set.class));
		System.out.println(typeMatchers.conversion(new Integer[]{10, 20, 30,20}, Set.class));
		System.out.println(typeMatchers.conversion(new Boolean[]{true, true, false}, Set.class));

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
		System.out.println(typeMatchers.conversion(200, List.class));

		System.out.println(typeMatchers.conversion(117, Animal.class));
		System.out.println(typeMatchers.conversion(118, Cat.class));
	}


	@Accessors(chain = true)
	@Data
	public static class Animal {
		private String name;
		public Animal() {

		}
		public Animal(String name) {
			this.name = name;
		}
	}

	@Accessors(chain = true)
	@Data
	private static class Cat extends Animal {
		private int age;
	}
}
