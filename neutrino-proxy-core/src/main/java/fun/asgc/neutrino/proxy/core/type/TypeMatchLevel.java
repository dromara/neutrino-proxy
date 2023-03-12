package fun.asgc.neutrino.proxy.core.type;

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
	EXTENSION(6, 10000000, 50000000, "扩展匹配"),
	// 自定义匹配
	CUSTOM(7, 100000000, 500000000, "自定义匹配"),
	// 不匹配
	NOT(8, Integer.MAX_VALUE, Integer.MAX_VALUE, "不匹配");
	private static Map<Integer,TypeMatchLevel> levelMap = Stream.of(TypeMatchLevel.values()).collect(Collectors.toMap(TypeMatchLevel::getLevel, Function.identity()));

	/**
	 * 匹配级别
	 */
	private int level;
	/**
	 * 类型距离最小值
	 */
	private int distanceMin;
	/**
	 * 类型距离最大值
	 */
	private int distanceMax;
	/**
	 * 描述
	 */
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
