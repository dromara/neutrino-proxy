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
package fun.asgc.neutrino.core.type.extension;

import fun.asgc.neutrino.core.type.TypeMatcher;
import fun.asgc.neutrino.core.type.TypeMatcherGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的扩展类型匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public abstract class AbstractExtensionMatcherGroup implements TypeMatcherGroup {
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

	public AbstractExtensionMatcherGroup(int distanceMin, int distanceMax) {
		this.distanceMin = distanceMin;
		this.distanceMax = distanceMax;
		this.matchers = new ArrayList<>();
	}

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
