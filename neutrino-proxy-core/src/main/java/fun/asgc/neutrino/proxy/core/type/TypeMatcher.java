
package fun.asgc.neutrino.proxy.core.type;

/**
 * 值类型匹配器
 *
 * @author: aoshiguchen
 * @date: 2022/6/17
 */
@FunctionalInterface
public interface TypeMatcher {

	/**
	 * 匹配
	 * @param clazz
	 * @param targetClass
	 * @return
	 */
	TypeMatchInfo match(Class<?> clazz, Class<?> targetClass);

}
