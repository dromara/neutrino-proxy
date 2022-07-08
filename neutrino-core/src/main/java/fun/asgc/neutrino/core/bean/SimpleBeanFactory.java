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

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.aop.Aop;
import fun.asgc.neutrino.core.aop.SingletonInterceptor;
import fun.asgc.neutrino.core.aop.interceptor.ExceptionHandler;
import fun.asgc.neutrino.core.aop.interceptor.Filter;
import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.aop.interceptor.ResultAdvice;
import fun.asgc.neutrino.core.exception.BeanException;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.util.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.Filer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
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

	/**
	 * 类集合
	 */
	private Set<Class<?>> classes = new HashSet<>();

	public SimpleBeanFactory(String name) {
		super(name);
	}

	public SimpleBeanFactory(AbstractBeanFactory parent, String name) {
		super(parent, name);
	}

	@Override
	protected BeanWrapper addBean(Class<?> type, String name) {
		log.debug("addBean[type:{} name:{}]", type.getName(), name);
		BeanWrapper beanWrapper = new BeanWrapper()
			.setType(type)
			.setName(name)
			.setLazy(Boolean.FALSE)
			.setOrder(Integer.MAX_VALUE)
			.setStatus(BeanStatus.REGISTER)
			.setInstantiationMode(BeanInstantiationMode.DIRECT)
			.setNonIntercept(false);
		Lazy lazy = ClassUtil.getAnnotation(type, Lazy.class);
		if (null != lazy) {
			beanWrapper.setLazy(lazy.value());
		}
		Order order = ClassUtil.getAnnotation(type, Order.class);
		if (null != order) {
			beanWrapper.setOrder(order.value());
		}
		// 默认不拦截
		if (ApplicationRunner.class.isAssignableFrom(type) ||
			type.isAnnotationPresent(Configuration.class) ||
			Interceptor.class.isAssignableFrom(type) ||
			Filer.class.isAssignableFrom(type) ||
			ExceptionHandler.class.isAssignableFrom(type) ||
			ResultAdvice.class.isAssignableFrom(type) ||
			BeanFactoryAware.class.isAssignableFrom(type)
		) {
			beanWrapper.setNonIntercept(true);
		}
		NonIntercept nonIntercept = ClassUtil.getAnnotation(type, NonIntercept.class);
		if (null != nonIntercept) {
			beanWrapper.setNonIntercept(nonIntercept.value());
		}

		addBean(beanWrapper);
		registerBeanForMethod(beanWrapper, type);
		return beanWrapper;
	}

	/**
	 * 根据method注册bean
	 * @param factoryBean
	 * @param type
	 */
	private void registerBeanForMethod(BeanWrapper factoryBean, Class<?> type) {
		Set<Method> methods = ReflectUtil.getMethods(type);
		if (CollectionUtil.isEmpty(methods)) {
			return;
		}
		for (Method method : methods) {
			Bean bean = method.getAnnotation(Bean.class);
			if (null == bean) {
				continue;
			}
			String beanName = bean.value();
			if (StringUtil.isEmpty(beanName)) {
				beanName = method.getName();
			}
			Class<?> beanType = method.getReturnType();
			if (method.getParameters().length > 0) {
				if (!(method.getParameters().length == 1 && method.getParameters()[0].isAnnotationPresent(Autowired.class))) {
					throw new BeanException(String.format("Bean[type:%s name:%s] 注册失败，Class:%s method:%s 方法仅支持最多带一个参数，且参数必须带`Autowired`注解!", beanType.getName(), beanName, type.getName(), method.getName()));
				}
			}
			BeanWrapper beanWrapper = addBean(beanType, beanName);
			beanWrapper.setFactoryBean(factoryBean);
			beanWrapper.setInstantiationMode(BeanInstantiationMode.METHOD);
			beanWrapper.setInstantiationMethod(method);
			beanWrapper.setNonIntercept(false);

			Lazy lazy = method.getAnnotation(Lazy.class);
			if (null != lazy) {
				beanWrapper.setLazy(lazy.value());
			}
			Order order = method.getAnnotation(Order.class);
			if (null != order) {
				beanWrapper.setOrder(order.value());
			}
		}
	}

	@Override
	protected void dependencyCheck(BeanWrapper bean) throws BeanException {
		try {
			LockUtil.doubleCheckProcess(
				() -> BeanStatus.REGISTER == bean.getStatus(),
				bean,
				() -> {
					// TODO
					bean.setStatus(BeanStatus.DEPENDENCY_CHECKING);
				}
			);
		} catch (Exception e) {
			throw new BeanException(String.format("Bean[type:%s name:%s] 依赖检测异常!", bean.getType().getName(), bean.getName()));
		}
	}

	@Override
	protected <T> T newInstance(BeanWrapper bean, Object... args) throws BeanException {
		try {
			return LockUtil.doubleCheckProcess(
				() -> BeanStatus.DEPENDENCY_CHECKING == bean.getStatus(),
				bean,
				() -> {
					if (BeanInstantiationMode.DIRECT == bean.getInstantiationMode()) {
						// 直接实例化
						if (ClassUtil.isInterface(bean.getType())) {
							Class<?> implementationType = findOneImplementationAndReturn(bean.getType());
							// 如果是一个接口，且有唯一实现类，则采用该实现类的实例作为bean。否则直接代理该接口
							if (null == implementationType) {
								bean.setInstance(Aop.get(bean.getType()));
							} else {
								if (ClassUtil.hasNoArgsConstructor(implementationType)) {
									if (bean.isNonIntercept()) {
										bean.setInstance(implementationType.newInstance());
									} else {
										bean.setInstance(Aop.get(implementationType, bean.getType()));
									}
								} else {
									// TODO 暂不支持有参构造器
									throw new BeanException(String.format("Bean[type:%s name:%s] 没有无参构造器，实例化失败!", bean.getType().getName(), bean.getName()));
								}
							}
						} else if(bean.getType().isAnnotationPresent(Configuration.class)) {
							bean.setInstance(ConfigUtil.getYmlConfig(bean.getType()));
						} else {
							if (ClassUtil.hasNoArgsConstructor(bean.getType())) {
								if (bean.isNonIntercept()) {
									bean.setInstance(bean.getType().newInstance());
								} else {
									bean.setInstance(Aop.get(bean.getType()));
								}
							} else {
								// TODO 暂不支持有参构造器
								throw new BeanException(String.format("Bean[type:%s name:%s] 没有无参构造器，实例化失败!", bean.getType().getName(), bean.getName()));
							}
						}
					} else if (BeanInstantiationMode.METHOD == bean.getInstantiationMode()) {
						// 通过bean方法实例化
						BeanWrapper factoryBean = bean.getFactoryBean();
						if (!factoryBean.hasInstance()) {
							newInstance(factoryBean);
						}
						if (bean.getInstantiationMethod().getParameters().length == 0) {
							bean.setInstance(bean.getInstantiationMethod().invoke(factoryBean.getInstance()));
						} else {
							// 暂时只处理一个参数的情况
							Autowired autowired = bean.getInstantiationMethod().getParameters()[0].getAnnotation(Autowired.class);
							String beanName = autowired.value();
							Class<?> beanType = bean.getInstantiationMethod().getParameters()[0].getType();
							Object arg = null;
							if (BeanMatchMode.ByName == autowired.matchMode()) {
								arg = getBean(beanName);
							} else if(BeanMatchMode.ByType == autowired.matchMode()) {
								arg = getBean(beanType);
							} else {
								arg = getBeanByTypeAndName(beanType, beanName);
							}
							bean.setInstance(bean.getInstantiationMethod().invoke(factoryBean.getInstance(), arg));
						}

					} else if (BeanInstantiationMode.FACTORY == bean.getInstantiationMode()) {
						// 通过FactoryBean实例化
						BeanWrapper factoryBean = bean.getFactoryBean();
						if (!factoryBean.hasInstance()) {
							newInstance(factoryBean);
						}
						bean.setInstance(((FactoryBean)factoryBean.getInstance()).getInstance());
					}

					if (null != bean.getInstance()) {
						bean.setStatus(BeanStatus.INSTANCE);
					}
				},
				() -> (T)bean.getInstance()
			);
		} catch (BeanException e) {
			throw e;
		}catch (Exception e) {
			throw new BeanException(String.format("Bean[type:%s name:%s] 实例化异常", bean.getType().getName(), bean.getName()), e);
		}
	}

	@Override
	protected void inject(BeanWrapper bean) throws BeanException {
		try {
			LockUtil.doubleCheckProcess(
				() -> BeanStatus.INSTANCE == bean.getStatus(),
				bean,
				() -> {
					Set<Field> fieldSet = ReflectUtil.getInheritChainDeclaredFieldSet(bean.getType());
					if (CollectionUtil.isEmpty(fieldSet)) {
						bean.setStatus(BeanStatus.INJECT);
						return;
					}
					for (Field field : fieldSet) {
						Autowired autowired = field.getAnnotation(Autowired.class);
						if (null == autowired) {
							continue;
						}
						String beanName = autowired.value();
						if (StringUtil.isEmpty(beanName)) {
							beanName = field.getName();
						}
						Class<?> parameterType = autowired.parameterTypes().length == 0 ? Object.class : autowired.parameterTypes()[0];
						BeanMatchMode matchMode = autowired.matchMode();
						Object obj = null;
						if (matchMode == BeanMatchMode.ByType) {
							if (TypeUtil.isListable(field.getType())) {
								List list = getBeanList(parameterType);
								obj = TypeUtil.listTo(list, field.getType());
							} else {
								obj = getBean(field.getType());
							}
						} else if(matchMode == BeanMatchMode.ByName) {
							if (TypeUtil.isListable(field.getType())) {
								List list = getBeanListByName(parameterType, beanName);
								obj = TypeUtil.listTo(list, field.getType());
							} else {
								obj = getBeanByName(field.getType(), beanName);
							}
						} else if (matchMode == BeanMatchMode.ByTypeName) {
							obj = getBeanByTypeAndName(field.getType(), beanName);
							if (TypeUtil.isListable(field.getType())) {
								obj = TypeUtil.listTo(Lists.newArrayList(obj), field.getType());
							}
						}
						if (null == obj) {
							throw new BeanException(String.format("Bean[type:%s name:%s field:%s] 注入异常", bean.getType().getName(), bean.getName(), field.getName()));
						}
						ReflectUtil.setFieldValue(field, bean.getInstance(), obj);
					}
					bean.setStatus(BeanStatus.INJECT);
				}
			);
		} catch (BeanException e){
			throw e;
		}catch (Exception e) {
			throw new BeanException(String.format("Bean[type:%s name:%s] 注入异常", bean.getType().getName(), bean.getName()), e);
		}
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

	public void register(Set<Class<?>> classes) {
		if (CollectionUtil.isEmpty(classes)) {
			return;
		}
		this.classes.addAll(classes);
		classes.stream()
			.filter(item -> ClassUtil.isAnnotateWith(item, Component.class)
				|| Interceptor.class.isAssignableFrom(item)
				|| Filter.class.isAssignableFrom(item)
				|| ExceptionHandler.class.isAssignableFrom(item)
				|| ResultAdvice.class.isAssignableFrom(item)
			)
			.forEach(clazz -> {
				String beanName = TypeUtil.getDefaultVariableName(clazz);
				Component component = ClassUtil.getAnnotation(clazz, Component.class);
				if (null != component && StringUtil.notEmpty(component.value())) {
					beanName = component.value();
				}
				registerBean(clazz, beanName);
			});
	}

	/**
	 * 查找指定接口的唯一实现类，若存在一个则返回
	 * @param clazz
	 * @return
	 */
	private Class<?> findOneImplementationAndReturn(Class<?> clazz) {
		if (!ClassUtil.isInterface(clazz) || CollectionUtil.isEmpty(this.classes)) {
			return null;
		}
		Set<Class<?>> cls = classes.stream().filter(c -> clazz.isAssignableFrom(c)
				&& !ClassUtil.isInterface(c) && !ClassUtil.isAbstract(c) && !ClassUtil.isStatic(c) && ClassUtil.isPublic(c)
		).collect(Collectors.toSet());
		if (cls.size() == 1) {
			return cls.iterator().next();
		}
		return null;
	}
}
