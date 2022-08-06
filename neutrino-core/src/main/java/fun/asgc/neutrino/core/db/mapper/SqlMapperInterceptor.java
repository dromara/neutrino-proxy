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
import fun.asgc.neutrino.core.annotation.Param;
import fun.asgc.neutrino.core.aop.Invocation;
import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.core.util.*;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
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
				Object params = getParams(inv);
				if (null == params || params.getClass().isArray()) {
					res = jdbcTemplate.queryForList(resultComponentType, sql, inv.getArgs());
				} else {
					res = jdbcTemplate.queryForListByMap(resultComponentType, sql, (Map)params);
				}
			} else {
				if (Page.class.isAssignableFrom(inv.getTargetMethod().getParameters()[0].getType())) {
					// 分页查询 TODO 此处暂时临时处理，假设后面的参数是一个DO对象
					Page page = (Page) inv.getArgs()[0];
//					Map<String, Object> params = new HashMap<>();
//					for (int i = 1; i < inv.getTargetMethod().getParameterCount(); i++) {
//						Parameter parameter = inv.getTargetMethod().getParameters()[i];
//						Param param = parameter.getAnnotation(Param.class);
//						if (null == param || StringUtil.isEmpty(param.value())) {
//							continue;
//						}
//						params.put(param.value(), inv.getArgs()[i]);
//					}
					int offset = (page.getCurrentPage() - 1) * page.getPageSize();
					long total = jdbcTemplate.queryForLongByModel(String.format("select count(1) from (%s) T", sql), inv.getArgs()[1]);
					List resultList = jdbcTemplate.queryForListByModel(resultComponentType, String.format("%s limit %s,%s", sql, offset, page.getPageSize()), inv.getArgs()[1]);
					page.setTotal(total);
					page.setRecords(resultList);
				} else {
					res = jdbcTemplate.query(resultType, sql, inv.getArgs());
				}
			}
		} else if (sqlParser.isInsert() || sqlParser.isDelete() || sqlParser.isUpdate()) {
			int argsCount = ArrayUtil.isEmpty(inv.getArgs()) ? 0 : inv.getArgs().length;
			if (argsCount == 1 && inv.getArgs()[0] instanceof Map) {
				res = jdbcTemplate.updateByMap(sql, (Map)inv.getArgs()[0]);
			} else if (argsCount == 1 && !TypeUtil.isNormalBasicType(inv.getArgs()[0].getClass())) {
				res = jdbcTemplate.updateByModel(sql, inv.getArgs()[0]);
			} else {
				Object params = getParams(inv);
				if (null == params) {
					res = jdbcTemplate.update(sql);
				} else if (params.getClass().isArray()){
					res = jdbcTemplate.update(sql, (Object[])params);
				} else if (params instanceof Map) {
					res = jdbcTemplate.updateByMap(sql, (Map)params);
				}
			}
		}
		inv.setReturnValue(TypeUtil.conversion(res, resultType));
	}

	/**
	 * 获取请求参数
	 * @param inv
	 * @return
	 */
	private Object getParams(Invocation inv) {
		if (inv.getTargetMethod().getParameterCount() == 0) {
			return null;
		}
		if (!inv.getTargetMethod().getParameters()[0].isAnnotationPresent(Param.class)) {
			return inv.getArgs();
		}
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < inv.getTargetMethod().getParameterCount(); i++) {
			Parameter parameter = inv.getTargetMethod().getParameters()[i];
			Param param = parameter.getAnnotation(Param.class);
			if (null == param) {
				continue;
			}
			String key = param.value();
			Object val = inv.getArgs()[i];
			if (!StringUtil.isEmpty(key)) {
				params.put(key, val);
			}

		}
		return params;
	}
}
