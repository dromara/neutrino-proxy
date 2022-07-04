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

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/1
 */
public interface BeanFactory {

	/**
	 * 尝试根据名称获取bean
	 * @param name
	 * @param args
	 * @return
	 * @throws BeanException
	 */
	<T> T getBean(String name, Object... args) throws BeanException;

	/**
	 * 尝试根据名称获取bean,此处的type不做强制限定，可以是bean的接口或超类型
	 * @param type
	 * @param name
	 * @param args
	 * @param <T>
	 * @return
	 */
	<T> T getBeanByName(Class<T> type, String name, Object... args);

	/**
	 * 尝试根据类型获取bean
	 * @param type
	 * @param args
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	<T> T getBean(Class<T> type, Object... args) throws BeanException;

	/**
	 * 尝试根据Bean身份标识获取bean
	 * @param identity
	 * @param args
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	<T> T getBean(BeanIdentity identity, Object... args) throws BeanException;

	/**
	 * 根据类型+名称获取bean
	 * @param type
	 * @param name
	 * @param args
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	<T> T getBeanByTypeAndName(Class<T> type, String name, Object... args) throws BeanException;

	/**
	 * 尝试根据类型获取bean
	 * @param type
	 * @param args
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	<T> List<T> getBeanList(Class<T> type, Object... args) throws BeanException;

	/**
	 * 尝试根据名称获取bean,此处的type不做强制限定，可以是bean的接口或超类型
	 * @param type
	 * @param name
	 * @param args
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	<T> List<T> getBeanListByName(Class<T> type, String name, Object... args) throws BeanException;
}
