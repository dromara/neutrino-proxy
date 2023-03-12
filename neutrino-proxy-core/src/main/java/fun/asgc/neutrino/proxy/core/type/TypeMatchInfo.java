package fun.asgc.neutrino.proxy.core.type;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Accessors(chain = true)
@Data
public class TypeMatchInfo {

	/**
	 * 目标类型类型
	 */
	private Class<?> targetType;

	/**
	 * 值类型
	 */
	private Class<?> valueType;

	/**
	 * 类型距离
	 */
	private int typeDistance;

	/**
	 * 类型匹配器
	 */
	private TypeMatcher typeMatcher;

	/**
	 * 类型转换器
	 */
	private TypeConverter typeConverter;

	public TypeMatchInfo(Class<?> valueType, Class<?> targetType, TypeMatcher typeMatcher) {
		this.valueType = valueType;
		this.targetType = targetType;
		this.typeMatcher = typeMatcher;
		this.typeDistance = TypeMatchLevel.NOT.getDistanceMin();
	}

	public boolean isNotMatch() {
		return !isMatched();
	}

	public boolean isMatched() {
		TypeMatchLevel matchLevel = TypeMatchLevel.byTypeDistance(this.typeDistance);
		return  matchLevel != null && matchLevel != TypeMatchLevel.NOT && typeConverter != null;
	}
}
