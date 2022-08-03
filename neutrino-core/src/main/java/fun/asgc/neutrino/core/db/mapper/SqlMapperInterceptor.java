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

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.aop.Invocation;
import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.core.util.*;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * sqlmapper拦截器
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
@NonIntercept
@Component
public class SqlMapperInterceptor implements Interceptor {
	@Override
	public void intercept(Invocation inv) throws Exception {
		JdbcTemplate jdbcTemplate = BeanManager.getBean(JdbcTemplate.class);
		Assert.notNull(jdbcTemplate, "JdbcTemplate未注入，调用失败!");
		SqlParser sqlParser = SqlParser.getInstance(inv.getTargetMethod());
		if (null == sqlParser) {
			return;
		}

		String sql = sqlParser.getSql();
		Class<?> resultType = sqlParser.getResultType();
		Class<?> resultComponentType = sqlParser.getResultComponentType();
		Object res = null;
		if (sqlParser.isSelect()) {
			if (sqlParser.isReturnCollection()) {
				res = jdbcTemplate.queryForList(resultComponentType, sql, inv.getArgs());
			} else {
				res = jdbcTemplate.query(resultType, sql, inv.getArgs());
			}
		} else if (sqlParser.isInsert() || sqlParser.isDelete() || sqlParser.isUpdate()) {
			int argsCount = ArrayUtil.isEmpty(inv.getArgs()) ? 0 : inv.getArgs().length;
			if (argsCount == 1 && inv.getArgs()[0] instanceof Map) {
				res = jdbcTemplate.updateByMap(sql, (Map)inv.getArgs()[0]);
			} else if (argsCount == 1 && !TypeUtil.isNormalBasicType(inv.getArgs()[0].getClass())) {
				res = jdbcTemplate.updateByModel(sql, inv.getArgs()[0]);
			} else {
				res = jdbcTemplate.update(sql, inv.getArgs());
			}
		}
		inv.setReturnValue(TypeUtil.conversion(res, resultType));
	}
}
