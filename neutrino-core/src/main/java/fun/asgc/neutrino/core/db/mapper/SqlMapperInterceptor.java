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
package fun.asgc.neutrino.core.db.mapper;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.aop.Invocation;
import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.StringUtil;

import java.util.Collection;

/**
 * sqlmapper拦截器
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
@Component
public class SqlMapperInterceptor implements Interceptor {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void intercept(Invocation inv) {
		Assert.notNull(jdbcTemplate, "JdbcTemplate未注入，调用失败!");
		if (inv.getTargetMethod().isAnnotationPresent(Select.class)) {
			Select select = inv.getTargetMethod().getAnnotation(Select.class);
			String sql = select.value();
			if (StringUtil.isEmpty(sql)) {
				throw new RuntimeException("sql不能为空!");
			}
			Class<?> returnType = inv.getReturnType();
			if (!Collection.class.isAssignableFrom(returnType)) {
				Object res = jdbcTemplate.query(returnType, sql, inv.getArgs());
				inv.setReturnValue(res);
			}
		}
	}
}
