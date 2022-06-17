/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 类型匹配等级
 * @author: aoshiguchen
 * @date: 2022/6/17
 */
@Getter
@AllArgsConstructor
public enum TypeMatchLevel {
	// 当且仅当匹配类为空（即匹配对象为null没有类型）时
	NULL(0, 0, 0, "空匹配"),
	// 当且仅当匹配类与目标类完全相等
	PERFECT(1, 1, 1, "完美匹配"),
	// 当且仅当匹配类为严格基本数据类型，且目标类为对应的包装类型
	PACKING(2, 100, 100, "装箱匹配"),
	// 当且仅当目标类为严格基本数据类型，且匹配类为对应的包装类型
	UNPACKING(3, 200,200,"拆箱匹配"),
	// 当且仅当匹配类和目标类均为一般基本数据类型（非严格），且目标类型的范围更加宽泛时
	ASCENSION(4, 1000, 2000, "类型提升匹配"),
	// 超类匹配，当且仅当目标类是匹配类的超类时
	SUPER(5, 1000000, 2000000, "超类匹配"),
	// 内置的扩展匹配
	EXTENSION(6, 10000000, 20000000, "扩展匹配"),
	// 自定义匹配
	CUSTOM(7, 100000000, 200000000, "自定义匹配");
	private static Map<Integer,TypeMatchLevel> levelMap = Stream.of(TypeMatchLevel.values()).collect(Collectors.toMap(TypeMatchLevel::getLevel, Function.identity()));

	private int level;
	private int distanceMin;
	private int distanceMax;
	private String desc;

	public static TypeMatchLevel byLevel(int level) {
		return levelMap.get(level);
	}

	public static TypeMatchLevel byTypeDistance(int typeDistance) {
		for (TypeMatchLevel item : values()) {
			if (typeDistance >= item.getDistanceMin() && typeDistance <= item.getDistanceMax()) {
				return item;
			}
		}
		return null;
	}
}
