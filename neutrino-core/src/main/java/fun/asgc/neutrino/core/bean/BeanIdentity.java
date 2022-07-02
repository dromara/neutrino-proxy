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

import fun.asgc.neutrino.core.util.Assert;

/**
 * 用于标识bean的身份
 *
 * 此处用名称+类型作为bean的唯一标识
 *
 * @author: aoshiguchen
 * @date: 2022/7/1
 */
public class BeanIdentity implements Identity {
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类型
	 */
	private Class<?> type;
	/**
	 * hashCode
	 */
	private int identityHashCode;

	public BeanIdentity(String name, Class<?> type) {
		Assert.notEmpty(name, "名称不能为空！");
		Assert.notNull(type, "类型不能为空!");
		this.name = name;
		this.type = type;
		this.identityHashCode = System.identityHashCode(name) + System.identityHashCode(type);
	}

	/**
	 * 获取名称
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 获取类型
	 * @return
	 */
	public Class<?> getType() {
		return this.type;
	}

	@Override
	public boolean isOnly() {
		return Boolean.TRUE;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof BeanIdentity)) {
			return false;
		}
		BeanIdentity beanIdentity = (BeanIdentity)obj;
		return this.name.equals(beanIdentity.getName()) && this.type.equals(beanIdentity.getType());
	}

	@Override
	public int hashCode() {
		return identityHashCode;
	}
}
