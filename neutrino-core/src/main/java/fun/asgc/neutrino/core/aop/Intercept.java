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
package fun.asgc.neutrino.core.aop;

import fun.asgc.neutrino.core.aop.interceptor.ExceptionHandler;
import fun.asgc.neutrino.core.aop.interceptor.Filter;
import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.aop.interceptor.ResultAdvice;

import java.lang.annotation.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/24
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Intercept {
	/**
	 * 指定拦截器
	 * @return
	 */
	Class<? extends Interceptor>[] value() default {};

	/**
	 * 排除拦截器
	 * @return
	 */
	Class<? extends Interceptor>[] exclude() default {};

	/**
	 * 忽略一切全局拦截器
	 * @return
	 */
	boolean ignoreGlobal() default false;

	/**
	 * 指定过滤器
	 * @return
	 */
	Class<? extends Filter>[] filter() default {};

	/**
	 * 指定异常处理器
	 * @return
	 */
	Class<? extends ExceptionHandler>[] exceptionHandler() default {};

	/**
	 * 指定结果处理器
	 * @return
	 */
	Class<? extends ResultAdvice>[] resultAdvice() default {};
}
