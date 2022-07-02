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

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Lazy;
import fun.asgc.neutrino.core.annotation.Order;
import fun.asgc.neutrino.core.exception.BeanException;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import fun.asgc.neutrino.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author: 一个简单的bean工厂实现
 * @date: 2022/7/2
 */
@Slf4j
public class SimpleBeanFactory extends AbstractBeanFactory {

	public SimpleBeanFactory(String name) {
		super(name);
	}

	public SimpleBeanFactory(AbstractBeanFactory parent, String name) {
		super(parent, name);
	}

	@Override
	protected void addBean(Class<?> type, String name) {
		log.debug("addBean[type:{} name:{}]", type.getName(), name);
		BeanWrapper beanWrapper = new BeanWrapper()
			.setType(type)
			.setName(name)
			.setLazy(Boolean.FALSE)
			.setOrder(Integer.MAX_VALUE)
			.setStatus(BeanStatus.REGISTER);
		Lazy lazy = ClassUtil.getAnnotation(type, Lazy.class);
		if (null != lazy) {
			beanWrapper.setLazy(lazy.value());
		}
		Order order = ClassUtil.getAnnotation(type, Order.class);
		if (null != order) {
			beanWrapper.setOrder(order.value());
		}
		addBean(beanWrapper);
	}

	@Override
	protected void dependencyCheck(BeanWrapper bean) throws Exception {
		LockUtil.doubleCheckProcess(
			() -> BeanStatus.REGISTER == bean.getStatus(),
			bean,
			() -> {
				Set<Field> fieldSet = ReflectUtil.getInheritChainDeclaredFieldSet(bean.getType());
				if (CollectionUtil.notEmpty(fieldSet)) {
					fieldSet.forEach(field -> {
						Autowired autowired = field.getAnnotation(Autowired.class);
						if (null == autowired) {
							return;
						}
						String name = field.getName();
						// TODO
					});
				}
			}
		);
	}

	@Override
	protected <T> T newInstance(BeanWrapper bean) throws BeanException {
		try {
			return LockUtil.doubleCheckProcess(
				() -> BeanStatus.DEPENDENCY_CHECKING == bean.getStatus(),
				bean,
				() -> {
					bean.getClass().newInstance();
				},
				() -> (T)bean.getInstance()
			);
		} catch (Exception e) {
			throw new BeanException(String.format("Bean[type:%s name:%s] 实例化异常", bean.getType().getName(), bean.getName()), e);
		}
	}

	@Override
	protected void inject(BeanWrapper bean) throws Exception {
		LockUtil.doubleCheckProcess(
			() -> BeanStatus.INSTANCE == bean.getStatus(),
			bean,
			() -> {
				// TODO
			}
		);
	}

	@Override
	public void init() {
		List<BeanWrapper> beanWrapperList = beanCache.values().stream()
			.filter(b -> b.getStatus().getStatus() < BeanStatus.INIT.getStatus())
			.filter(b -> ApplicationRunner.class.isAssignableFrom(b.getType()) || !b.isLazy() || b.getOrder() <= 0)
			.collect(Collectors.toList());
		if (CollectionUtil.notEmpty(beanWrapperList)) {
			getOrNew(beanWrapperList);
		}
		super.init();
	}
}
