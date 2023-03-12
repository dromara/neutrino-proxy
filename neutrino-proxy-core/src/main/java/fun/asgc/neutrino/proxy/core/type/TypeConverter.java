package fun.asgc.neutrino.proxy.core.type;

/**
 * 类型转换器
 * @author: aoshiguchen
 * @date: 2022/6/17
 */
@FunctionalInterface
public interface TypeConverter {
	/**
	 * 类型转换
	 * @param value
	 * @param targetType
	 * @return
	 */
	Object convert(Object value, Class<?> targetType);
}
