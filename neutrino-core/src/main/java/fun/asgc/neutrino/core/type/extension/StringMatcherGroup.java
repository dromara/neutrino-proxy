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

import fun.asgc.neutrino.core.type.AbstractMatcherGroup;
import fun.asgc.neutrino.core.type.TypeMatchInfo;
import fun.asgc.neutrino.core.type.TypeMatcher;
import fun.asgc.neutrino.core.util.TypeUtil;

/**
 * 字符串匹配器组
 * @author: aoshiguchen
 * @date: 2022/6/29
 */
public class StringMatcherGroup extends AbstractMatcherGroup {

	public StringMatcherGroup(int distanceMin, int distanceMax) {
		super(distanceMin, distanceMax);
	}

	@Override
	public void init() {
		// 基本类型 -> String
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isNormalBasicType(clazz) && TypeUtil.isString(targetClass)) {
					matchInfo.setTypeDistance(getDistanceMin());
					matchInfo.setTypeConverter(((value, targetType) -> String.valueOf(value)));
				}
				return matchInfo;
			}
		});
		// String -> char
		add(new TypeMatcher() {
			@Override
			public TypeMatchInfo match(Class<?> clazz, Class<?> targetClass) {
				TypeMatchInfo matchInfo = new TypeMatchInfo(clazz, targetClass, this);
				if (TypeUtil.isChar(targetClass) && TypeUtil.isString(clazz)) {
					matchInfo.setTypeDistance(getDistanceMin() + 1);
					matchInfo.setTypeConverter(((value, targetType) -> ((String)value).charAt(0)));
				}
				return matchInfo;
			}
		});
	}

}
