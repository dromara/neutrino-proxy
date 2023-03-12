package fun.asgc.neutrino.proxy.core.type;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public abstract class AbstractMatcherGroup implements TypeMatcherGroup {
	/**
	 * 最小距离
	 */
	protected int distanceMin;

	/**
	 * 最大距离
	 */
	protected int distanceMax;

	/**
	 * 匹配器列表
	 */
	private List<TypeMatcher> matchers;

	public AbstractMatcherGroup(int distanceMin, int distanceMax) {
		this.distanceMin = distanceMin;
		this.distanceMax = distanceMax;
		this.matchers = new ArrayList<>();
		this.init();
	}

	protected abstract void init();

	@Override
	public List<TypeMatcher> matchers() {
		return matchers;
	}

	public synchronized void add(TypeMatcher matcher) {
		if (null != matcher) {
			this.matchers.add(matcher);
		}
	}

	@Override
	public int getDistanceMin() {
		return distanceMin;
	}

	@Override
	public int getDistanceMax() {
		return distanceMax;
	}
}
