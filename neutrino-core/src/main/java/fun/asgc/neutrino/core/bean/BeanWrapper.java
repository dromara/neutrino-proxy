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

import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.context.LifeCycle;
import fun.asgc.neutrino.core.util.*;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Accessors(chain = true)
@Data
public class BeanWrapper implements LifeCycle {
	private String name;
	private Class<?> type;
	private Object instance;
	private Component component;
	private int order;
	private boolean isBoot;
	private volatile boolean isInit;
	private BeanStatus status;
	private boolean isLazy;
	private BeanIdentity beanIdentity;
	private BeanWrapper factoryBean;
	private BeanInstantiationMode instantiationMode;
	private Method instantiationMethod;
	private boolean isNonIntercept;

	public boolean hasInstance() {
		return null != instance;
	}

	@Override
	public void init() {
		if (BeanStatus.INJECT != status) {
			return;
		}
		this.setStatus(BeanStatus.INIT);
		log.debug("Bean[type:{} name:{}]初始化...", getType().getName(), getName());
		isInit = true;

		Set<Method> methods = ReflectUtil.getMethods(type);
		if (CollectionUtil.notEmpty(methods)) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(Init.class) && method.getParameters().length == 0) {
					try {
						method.invoke(instance);
					} catch (Exception e) {
						log.error(String.format("Bean[type:%s name:%s]初始化方法执行异常 method:%s", type.getName(), name, method.getName()), e);
					}
				}
			}
		}
	}

	@Override
	public void destroy() {
		if (!hasInstance()) {
			return;
		}
		Set<Method> methods = ReflectUtil.getMethods(type);
		if (CollectionUtil.notEmpty(methods)) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(Destroy.class) && method.getParameters().length == 0) {
					try {
						method.invoke(instance);
					} catch (Exception e) {
						log.error(String.format("Bean[type:%s name:%s]销毁方法执行异常 method:%s", type.getName(), name, method.getName()), e);
					}
				}
			}
		}
	}

	public boolean isLazy() {
		Lazy lazy = ClassUtil.getAnnotation(type, Lazy.class);
		return null != lazy;
	}

	private boolean isDependOn(Class<?> target) {
		if (null == target) {
			return false;
		}
		Set<Field> fieldSet = ReflectUtil.getInheritChainDeclaredFieldSet(type);
		if (CollectionUtil.isEmpty(fieldSet)) {
			return false;
		}
		for (Field field : fieldSet) {
			Autowired autowired = field.getAnnotation(Autowired.class);
			if (null == autowired) {
				continue;
			}
			if (field.getType() == target) {
				return true;
			}
		}
		return false;
	}

	public void run(String[] args) throws Exception {
		if (BeanStatus.INIT != status) {
			return;
		}
		this.setStatus(BeanStatus.RUNNING);
		if (this.instance instanceof ApplicationRunner) {
			log.debug("Bean[type:{} name:{}]运行...", getType().getName(), getName());
			((ApplicationRunner)this.instance).run(args);
		}
	}

	public BeanIdentity getIdentity() {
		return LockUtil.doubleCheckProcessForNoException(
			() -> null == beanIdentity,
			this,
			() -> beanIdentity = new BeanIdentity(this.name, this.type),
			() -> beanIdentity
		);
	}
}
