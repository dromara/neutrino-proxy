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
package fun.asgc.neutrino.core.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bean的几种状态
 * @author: aoshiguchen
 * @date: 2022/7/2
 */
@Getter
@AllArgsConstructor
public enum BeanStatus {
	REGISTER(1, "已注册"),
	DEPENDENCY_CHECKING(2, "依赖关系检测完成"),
	INSTANCE(3, "已实例化"),
	INJECT(4, "已完成注入"),
	INIT(5, "已初始化"),
	RUNNING(6, "运行中"),
	DESTROY(7, "已销毁");
	private static final Map<Integer, BeanStatus> cache = Stream.of(BeanStatus.values()).collect(Collectors.toMap(BeanStatus::getStatus, Function.identity()));

	private Integer status;
	private String desc;

	public static BeanStatus of(Integer status) {
		return cache.get(status);
	}
}
