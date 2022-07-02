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
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.db.annotation.*;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.core.util.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * sqlmapper拦截器
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
@Component
public class SqlMapperInterceptor implements Interceptor {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final Cache<Method, Params> paramsCache = new MemoryCache<>();

	@Override
	public void intercept(Invocation inv) throws Exception {
		Assert.notNull(jdbcTemplate, "JdbcTemplate未注入，调用失败!");
		Params params = getParams(inv.getTargetMethod());
		if (null == params) {
			return;
		}

		String sql = params.getSql();
		Class<?> resultType = params.getResultType();
		Object res = null;
		if (params.isSelect()) {
			if (params.isReturnCollection()) {
				res = jdbcTemplate.queryForList(resultType, sql, inv.getArgs());
			} else {
				res = jdbcTemplate.query(resultType, sql, inv.getArgs());
			}
		} else if (params.isInsert() || params.isDelete() || params.isUpdate()) {
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

	/**
	 * 获取参数
	 * @param method
	 * @return
	 */
	private static Params getParams(Method method) throws Exception {
		return LockUtil.doubleCheckProcess(
			() -> !paramsCache.containsKey(method),
			method,
			() -> {
				String sign = String.format("%s#%s", method.getDeclaringClass().getName(), method.getName());
				Params params = null;
				ResultType resultType = method.getAnnotation(ResultType.class);
				Class<?> resultClass = (null == resultType) ? null : resultType.value();

				if (method.isAnnotationPresent(Select.class)) {
					// 查询
					Select select = method.getAnnotation(Select.class);
					String sql = select.value();
					if (StringUtil.isEmpty(sql)) {
						throw new RuntimeException(String.format("%s sql不能为空!", sign));
					}
					boolean isReturnCollection = Collection.class.isAssignableFrom(method.getReturnType());
					if (isReturnCollection && null == resultClass) {
						throw new RuntimeException(String.format("%s 请指名实体类型!", sign));
					}
					params = new Params().setSql(sql).setSelect(true).setResultType(resultClass).setReturnCollection(isReturnCollection);
				} else if (method.isAnnotationPresent(Insert.class)) {
					// 新增
					Insert insert = method.getAnnotation(Insert.class);
					String sql = insert.value();
					if (StringUtil.isEmpty(sql)) {
						throw new RuntimeException(String.format("%s sql不能为空!", sign));
					}
					params = new Params().setSql(sql).setInsert(true).setResultType(resultClass);
				} else if (method.isAnnotationPresent(Delete.class)) {
					// 删除
					Delete delete = method.getAnnotation(Delete.class);
					String sql = delete.value();
					if (StringUtil.isEmpty(sql)) {
						throw new RuntimeException(String.format("%s sql不能为空!", sign));
					}
					params = new Params().setSql(sql).setDelete(true).setResultType(resultClass);
				} else if (method.isAnnotationPresent(Update.class)) {
					// 更新
					Update update = method.getAnnotation(Update.class);
					String sql = update.value();
					if (StringUtil.isEmpty(sql)) {
						throw new RuntimeException(String.format("%s sql不能为空!", sign));
					}
					params = new Params().setSql(sql).setUpdate(true).setResultType(resultClass);
				}
				if (null == params) {
					throw new RuntimeException(String.format("%s 缺失SQL注解!", sign));
				}
				if (null == params.getResultType() && !Collection.class.isAssignableFrom(method.getReturnType())) {
					params.setResultType(method.getReturnType());
				}
				paramsCache.set(method, params);
			},
			() -> paramsCache.get(method)
		);
	}

	@Accessors(chain = true)
	@Data
	static class Params {
		private String sql;
		private Class<?> resultType;
		private boolean isReturnCollection;
		private boolean isSelect;
		private boolean isDelete;
		private boolean isUpdate;
		private boolean isInsert;
	}
}
