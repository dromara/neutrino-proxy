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

package fun.asgc.neutrino.core.util;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.bean.factory.BeanFactory;
import fun.asgc.neutrino.core.bean.factory.BeanFactoryAware;
import fun.asgc.neutrino.core.bean.BeanIdentity;
import fun.asgc.neutrino.core.exception.BeanException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Component
public class BeanManager implements BeanFactoryAware {
	private static volatile BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		BeanManager.beanFactory = beanFactory;
	}

	public static Object getBean(String name) {
		if (null == beanFactory) {
			throw new RuntimeException("BeanFactory尚未初始化");
		}
		return beanFactory.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		if (null == beanFactory) {
			throw new RuntimeException("BeanFactory尚未初始化");
		}
		return beanFactory.getBean(clazz);
	}

	/**
	 * 尝试根据Bean身份标识获取bean
	 * @param identity
	 * @param <T>
	 * @return
	 * @throws BeanException
	 */
	public static <T> T getBean(BeanIdentity identity) {
		if (null == beanFactory) {
			throw new RuntimeException("BeanFactory尚未初始化");
		}
		return beanFactory.getBean(identity);
	}

	/**
	 * 根据超类获取bean集合
	 * @param superClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> getBeanListBySuperClass(Class<T> superClass) {
		if (null == beanFactory) {
			throw new RuntimeException("BeanFactory尚未初始化");
		}
		return beanFactory.getBeanList(superClass);
	}

}
