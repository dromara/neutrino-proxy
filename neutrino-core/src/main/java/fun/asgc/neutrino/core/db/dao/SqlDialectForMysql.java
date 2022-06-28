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
package fun.asgc.neutrino.core.db.dao;

import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.db.template.DbCache;
import fun.asgc.neutrino.core.db.template.SqlAndParams;
import fun.asgc.neutrino.core.util.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class SqlDialectForMysql implements SqlDialect {
	private static final String TEMPLATE_FIND = "select * from `${tableName}` ${where}";
	private static final String TEMPLATE_COUNT = "select count(*) from `${tableName}` ${where}";
	private static final String TEMPLATE_LIMIT = "limit ${offset},${size}";
	private static final String TEMPLATE_INSERT = "insert into `${tableName}`(${columns}) values ${values}";
	private static final String TEMPLATE_UPDATE = "update `${tableName}` ${set} ${where}`";
	private static final String TEMPLATE_DELETE = "delete from `${tableName}` ${where}";

	@Override
	public SqlAndParams add(Object obj) {
		LinkedHashMap<String, Object> params = getParams(obj);
		Set<String> filterColumns = params.keySet();
		String sql = templateProcess(TEMPLATE_INSERT, new HashMap<String, String>(){
			{
				this.put("tableName", getTableName(obj));
				this.put("columns", buildSqlColumns(filterColumns));
				this.put("values", getSqlValue(filterColumns, params));
			}
		});
		return new SqlAndParams(sql);
	}

	@Override
	public SqlAndParams find(Class<?> clazz, LinkedHashMap<String, Object> params) {
		String sql = templateProcess(TEMPLATE_FIND, new HashMap<String, String>(){
			{
				this.put("tableName", getTableName(clazz));
				this.put("where", buildSqlWhere(null, params));
			}
		});
		return new SqlAndParams(sql, params);
	}

	@Override
	public SqlAndParams count(Class<?> clazz, LinkedHashMap<String, Object> params) {
		String sql = templateProcess(TEMPLATE_COUNT, new HashMap<String, String>(){
			{
				this.put("tableName", getTableName(clazz));
				this.put("where", buildSqlWhere(null, params));
			}
		});
		return new SqlAndParams(sql, params);
	}

	/**
	 * 获取表名
	 * @param obj
	 * @return
	 */
	private static String getTableName(Object obj) {
		Assert.notNull(obj, "对象不能为空!");
		return DbCache.toTableName(obj.getClass());
	}

	/**
	 * 获取表名
	 * @param clazz
	 * @return
	 */
	private static String getTableName(Class clazz) {
		Assert.notNull(clazz, "类不能为空!");
		return DbCache.toTableName(clazz);
	}

	/**
	 * 获取参数
	 * @param obj
	 * @return
	 */
	private static LinkedHashMap<String, Object> getParams(Object obj) {
		return getParams(obj, null);
	}

	/**
	 * 获取参数
	 * @param obj
	 * @return
	 */
	private static LinkedHashMap<String, Object> getParams(Object obj, Set<String> excludeFieldName) {
		Assert.notNull(obj, "对象不能为空!");
		Cache<Field, String> cache = DbCache.getFieldCache(obj.getClass());
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		if (null == cache || cache.isEmpty()) {
			return params;
		}
		Set<String> tmp = new HashSet<>();
		for (Field field : cache.keySet()) {
			String column = cache.get(field);
			if (tmp.contains(column)) {
				continue;
			}
			if (CollectionUtil.notEmpty(excludeFieldName) && excludeFieldName.contains(field.getName())) {
				continue;
			}
			tmp.add(column);
			Object value = ReflectUtil.getFieldValue(field, obj);
			params.put(column, value);
		}
		return params;
	}

	/**
	 * 拼接where条件
	 * @param filterColumns
	 * @return
	 */
	private static String buildSqlWhere(Set<String> filterColumns, LinkedHashMap<String, Object> params) {
		List<String> sql = new ArrayList<>();
		if (CollectionUtil.isEmpty(params)) {
			return "";
		}
		for (String column : params.keySet()) {
			if (null != filterColumns && !filterColumns.contains(column)) {
				continue;
			}
			Object value = params.get(column);
			if (null == value) {
				sql.add(String.format("`%s` is null", column));
			} else {
				sql.add(String.format("`%s` = :%s", column, column));
			}
		}
		if (sql.isEmpty()) {
			return "";
		}
		return "where " + sql.stream().collect(Collectors.joining(" and "));
	}

	/**
	 * 拼接sql设置值的部分
	 * @param filterColumns
	 * @param params
	 * @return
	 */
	private static String buildSqlSet(Set<String> filterColumns, LinkedHashMap<String, Object> params) {
		List<String> sql = new ArrayList<>();
		if (CollectionUtil.isEmpty(filterColumns)) {
			return "";
		}
		for (String column : filterColumns) {
			Object value = params.get(column);
			if (null == value) {
				sql.add(String.format("`%s` = null"));
			} else {
				sql.add(String.format("%s = :%s", column, column));
			}
		}
		if (sql.isEmpty()) {
			return "";
		}
		return "set " + sql.stream().collect(Collectors.joining(","));
	}

	/**
	 * 拼接字段
	 * @param filterColumns
	 * @return
	 */
	private static String buildSqlColumns(Set<String> filterColumns) {
		return filterColumns.stream().map(s -> String.format("`%s`", s)).collect(Collectors.joining(","));
	}

	/**
	 * 值处理
	 * @param value
	 * @return
	 */
	private static String sqlValueProcess(Object value) {
		if (null == value) {
			return "null";
		} else if (value instanceof Date) {
			return String.format("'%s'", DateUtil.format((Date)value, "yyyy-MM-dd HH:mm:ss"));
		}
		String res = String.valueOf(value).replaceAll("'", "\\\\\\\\'");

		return "'" + res + "'";
	}

	/**
	 * 获取values后面的sql语句
	 * @param filterColumns
	 * @param params
	 * @return
	 */
	private static String getSqlValue(Set<String> filterColumns, LinkedHashMap<String, Object> params) {
		List<String> list = new ArrayList<>();
		for (String column : filterColumns) {
			list.add(sqlValueProcess(params.get(column)));
		}
		return "(" + list.stream().collect(Collectors.joining(",")) + ")";
	}

	/**
	 * sql模板处理
	 * @param template
	 * @param params
	 * @return
	 */
	private static String templateProcess(String template, Map<String, String> params) {
		if (StringUtil.isEmpty(template) || CollectionUtil.isEmpty(params)) {
			return template;
		}
		for (String key : params.keySet()) {
			template = template.replaceAll(String.format("\\$\\{%s\\}", key), params.get(key));
		}
		return template;
	}
}
