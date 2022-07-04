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
import fun.asgc.neutrino.core.aop.Aop;
import fun.asgc.neutrino.core.context.LifeCycle;
import fun.asgc.neutrino.core.container.BeanContainer;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.context.ApplicationContext;
import fun.asgc.neutrino.core.context.Environment;
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

	public boolean hasInstance() {
		return null != instance;
	}

	@Override
	public void init() {
		if (BeanStatus.INJECT != status) {
			return;
		}
		this.setStatus(BeanStatus.INIT);
		log.info("Bean[type:{} name:{}]初始化...", getType().getName(), getName());
		isInit = true;

		Set<Method> methods = ReflectUtil.getMethods(type);
		if (CollectionUtil.notEmpty(methods)) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(Init.class) && method.getParameters().length == 0) {
					try {
						method.invoke(instance);
					} catch (Exception e) {
						log.error(String.format("初始化方法执行异常 class:%s method:%s", type.getName(), method.getName()), e);
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
						log.error(String.format("销毁方法执行异常 class:%s method:%s", type.getName(), method.getName()), e);
					}
				}
			}
		}
	}

	public boolean newInstance(ApplicationContext context) {
		boolean result = newInstance0();
		if (result) {
			inject(context);
			init();
		}
		return result;
	}

	private boolean newInstance0() {
		try {
			return LockUtil.doubleCheckProcess(() -> !hasInstance(),
				this,
				() -> {
					try {
						// 暂时先只支持yml配置
						Configuration configuration = type.getAnnotation(Configuration.class);
						if (null != configuration) {
							instance = ConfigUtil.getYmlConfig(type);
						} else if (ClassUtil.isInterface(type)) {
							instance = Aop.get(type);
						} else {
							// 由编码规避没有无参构造器的问题
							instance = type.newInstance();
						}
					} catch (Exception e) {
						// ignore
					}
				},
				() -> hasInstance()
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void inject(ApplicationContext context) {
		Set<Method> methods = ReflectUtil.getMethods(type);
		if (CollectionUtil.notEmpty(methods)) {
			methods.stream().forEach(method -> {
				fun.asgc.neutrino.core.annotation.Bean bean = method.getAnnotation(fun.asgc.neutrino.core.annotation.Bean.class);
				if (null == bean) {
					return;
				}
				String name = bean.value();
				if (StringUtil.isEmpty(name)) {
					name = method.getName();
				}
				// TODO 方法带参数的情况、class重复的问题，暂时由编码规避
				if (method.getParameters().length == 0) {
					try {
						Object obj = method.invoke(this.instance);
						if (null == obj) {
							log.error("bean 实例不能为空!");
							return;
						}
						context.getBeanContainer().addBean(new BeanWrapper()
							.setType(obj.getClass())
							.setBoot(false)
							.setComponent(null)
							.setInstance(obj)
							.setName(name)
							.setOrder(Integer.MAX_VALUE)
						);
					} catch (Exception e) {
						log.error(String.format("bean [%s] 实例化失败!", name), e);
					}
				}
			});
		}
		Set<Field> fieldSet = ReflectUtil.getInheritChainDeclaredFieldSet(type);
		if (CollectionUtil.notEmpty(fieldSet)) {
			fieldSet.forEach(field -> {
				Autowired autowired = field.getAnnotation(Autowired.class);
				if (null == autowired) {
					return;
				}
				if (field.getType() == Environment.class) {
					ReflectUtil.setFieldValue(field, instance, context.getEnvironment());
				} else if(field.getType() == ApplicationConfig.class) {
					ReflectUtil.setFieldValue(field, instance, context.getApplicationConfig());
				} else if(field.getType() == BeanContainer.class) {
					ReflectUtil.setFieldValue(field, instance, context.getBeanContainer());
				} else if (field.getType() == ApplicationContext.class) {
					ReflectUtil.setFieldValue(field, instance, context);
				} else {
					Class<?> autowiredType = field.getType();
					BeanWrapper autowiredBean = null;
					if (StringUtil.isEmpty(autowired.value())) {
						autowiredBean = context.getBeanContainer().getBean(autowiredType);
					} else {
						autowiredBean = context.getBeanContainer().getBean(autowired.value());
					}
					if (null == autowiredBean) {
						throw new RuntimeException(String.format("类 %s 自动装配字段:%s 依赖bean不存在!", type.getName(), field.getName()));
					}
					if (autowiredBean.isDependOn(type)) {
						throw new RuntimeException(String.format("类[%s]与类[%s]存在循环依赖!", type.getName(), field.getType().getName()));
					}
					autowiredBean.newInstance(context);
					ReflectUtil.setFieldValue(field, instance, autowiredBean.getInstance());
				}
			});
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

	public void run() {

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
