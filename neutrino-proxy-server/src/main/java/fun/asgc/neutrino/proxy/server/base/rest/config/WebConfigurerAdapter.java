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
package fun.asgc.neutrino.proxy.server.base.rest.config;

import fun.asgc.neutrino.core.annotation.Configuration;
import fun.asgc.neutrino.core.web.config.WebMvcConfigurer;
import fun.asgc.neutrino.core.web.interceptor.ExceptionHandlerRegistry;
import fun.asgc.neutrino.core.web.interceptor.InterceptorRegistry;
import fun.asgc.neutrino.core.web.interceptor.RestControllerAdviceHandler;
import fun.asgc.neutrino.proxy.server.base.rest.interceptor.*;

/**
 * web配置
 * @author: aoshiguchen
 * @date: 2022/7/30
 */
@Configuration
public class WebConfigurerAdapter implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new BaseAuthInterceptor())
			.addPathPatterns("/**").excludePathPatterns("/**/*.html", "/**/*.js", "/**/*.ico");
		registry.addInterceptor(new CorsInterceptor());
		registry.addInterceptor(new VisitLogInterceptor());
	}

	@Override
	public RestControllerAdviceHandler adviceHandler() {
		return new GlobalAdviceHandler();
	}

	@Override
	public void addExceptionHandler(ExceptionHandlerRegistry registry) {
		registry.addExceptionHandler(new GlobalExceptionHandler());
	}
}
