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
package fun.asgc.neutrino.core.aop.proxy;

import fun.asgc.neutrino.core.base.GlobalConfig;
import fun.asgc.neutrino.core.util.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Asgc代理（子类代理）
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
@Slf4j
public class AsgcProxyFactory implements ProxyFactory {
	private static final String SYMBOLIC = "AsgcProxy$$";
	private static final String classNameTemplate = "%s" + SYMBOLIC + "%s";
	private static AtomicLong proxyClassCounter = new AtomicLong();
	private ProxyCompiler compiler = new ProxyCompiler();
	private ProxyClassLoader classLoader = new ProxyClassLoader();

	@Override
	public <T> T get(Class<T> targetType) throws Exception {
		Assert.notNull(targetType, "被代理类不能为空！");
		Assert.isTrue(canProxy(targetType), String.format("类[%s]无法被代理!", targetType.getName()));
		return doGet(targetType, targetType);
	}

	@Override
	public boolean canProxy(Class<?> clazz) {
		return ClassUtil.isInterface(clazz) ||
			(!ClassUtil.isFinal(clazz)
			&& !ClassUtil.isAbstract(clazz)
			&& ClassUtil.isPublic(clazz)
			&& !ClassUtil.isStatic(clazz)
			&& !ClassUtil.isInterface(clazz)
			&& Stream.of(clazz.getConstructors()).filter(e -> e.getParameterCount() == 0).count() > 0);
	}

	@Override
	public <T, P> P get(Class<T> targetType, Class<P> proxyType) throws Exception {
		Assert.notNull(targetType, "被代理类不能为空！");
		Assert.notNull(proxyType, "代理类类型不能为空！");
		Assert.isTrue(canProxy(targetType, proxyType), String.format("类[targetType:%s, proxyType:%s]无法被代理!", targetType.getName(), proxyType.getName()));
		return doGet(targetType, proxyType);
	}

	@Override
	public boolean canProxy(Class<?> targetType, Class<?> proxyType) {
		return canProxy(targetType) && proxyType.isAssignableFrom(targetType);
	}

	@Override
	public boolean isProxyClass(Class<?> clazz) {
		return null != clazz && clazz.getSimpleName().contains(SYMBOLIC);
	}

	private <T,P> P doGet(Class<T> targetType, Class<P> proxyType) throws ReflectiveOperationException {
		ProxyClass proxyClass = new ProxyClass(targetType);
		proxyClass.setName(generateClassName(targetType));
		String sourceCode = AsgcProxyGenerator.getInstance().generator(proxyClass.getName(), targetType, proxyType); // generateProxyClassSourceCode(clazz, proxyClass.getName());
		proxyClass.setSourceCode(sourceCode);
		if (GlobalConfig.isIsPrintGeneratorCode()) {
			log.debug("类:{} 的代理类源码:\n{}", targetType.getName(), sourceCode);
		}

		compiler.compile(proxyClass);
		Class<P> retClass = (Class<P>)classLoader.loadProxyClass(proxyClass);
		P obj = retClass.newInstance();
		return obj;
	}

	private String generateClassName(Class<?> clazz) {
		return String.format(classNameTemplate, clazz.getSimpleName(), proxyClassCounter.incrementAndGet());
	}
}
