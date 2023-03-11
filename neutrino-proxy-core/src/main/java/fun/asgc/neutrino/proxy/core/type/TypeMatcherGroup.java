package fun.asgc.neutrino.proxy.core.type;

import java.util.List;

/**
 * 匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public interface TypeMatcherGroup {
	/**
	 * 匹配器列表
	 * @return
	 */
	List<TypeMatcher> matchers();

	/**
	 * 获取最小距离
	 * @return
	 */
	int getDistanceMin();

	/**
	 * 获取最大距离
	 * @return
	 */
	int getDistanceMax();
}
