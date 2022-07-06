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

import fun.asgc.neutrino.core.exception.BeanException;

/**
 * bean注册表
 * @author: aoshiguchen
 * @date: 2022/7/1
 */
public interface BeanRegistry {

	/**
	 * 注册bean
	 * @param obj
	 * @throws BeanException
	 */
	void registerBean(Object obj) throws BeanException;

	/**
	 * 注册bean
	 * @param obj
	 * @param name
	 * @throws BeanException
	 */
	void registerBean(Object obj, String name) throws BeanException;

	/**
	 * 注册bean
	 * @param type
	 * @throws BeanException
	 */
	void registerBean(Class<?> type) throws BeanException;

	/**
	 * 注册bean
	 * @param type
	 * @param name
	 * @throws BeanException
	 */
	void registerBean(Class<?> type, String name) throws BeanException;

	/**
	 * 判断是否包含此bean
	 * @param type
	 * @param name
	 * @return
	 */
	boolean hasBean(Class<?> type, String name);

	/**
	 * 判断是否包含此bean
	 * @param type
	 * @return
	 */
	boolean hasBean(Class<?> type);

	/**
	 * 查询bean容器中有多少个叫该名称的bean
	 * @param name
	 * @return
	 */
	int countBean(String name);

	/**
	 * 查询bean容器中有多少个该类型的bean
	 * @param type
	 * @return
	 */
	int countBean(Class<?> type);
}
