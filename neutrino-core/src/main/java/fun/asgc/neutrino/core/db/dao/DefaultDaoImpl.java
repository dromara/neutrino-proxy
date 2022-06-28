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

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.db.annotation.Id;
import fun.asgc.neutrino.core.db.template.DataSourceHolder;
import fun.asgc.neutrino.core.db.template.DbCache;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.core.db.template.SqlAndParams;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.ReflectUtil;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * TODO 暂时先实现一部分，需要放下来思考更好的方式
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class DefaultDaoImpl<T> implements Dao<T> {

	/**
	 * 数据源持有者
	 */
	private DataSourceHolder dataSourceHolder;
	/**
	 * jdbc操作工具
	 */
	private JdbcTemplate jdbcTemplate;
	/**
	 * 实体类
	 */
	private Class<T> entryClass;
	/**
	 * sql方言
	 */
	private SqlDialect sqlDialect;
	/**
	 * id字段
	 */
	private Field idField;
	/**
	 * id列名
	 */
	private String idColumnName;

	public DefaultDaoImpl(DataSource dataSource, DBType dbType, Class<T> entryClass) {
		this(new DataSourceHolder(dataSource, dbType), entryClass);
	}

	public DefaultDaoImpl(DataSourceHolder dataSourceHolder, Class<T> entryClass) {
		Assert.notNull(dataSourceHolder, "数据源持有者不能为空!");
		Assert.notNull(dataSourceHolder.getDbType(), "数据库类型不能为空!");
		Assert.notNull(entryClass, "实体类不能为空!");

		this.dataSourceHolder = dataSourceHolder;
		this.jdbcTemplate = new JdbcTemplate(this.dataSourceHolder);
		this.entryClass = entryClass;
		this.sqlDialect = SqlDialectFactory.getSqlDialect(this.dataSourceHolder.getDbType());
		this.idField = getIdField(this.entryClass);
		Assert.notNull(idField, "实体类必须存在主键");
		this.idColumnName = DbCache.getColumnNameByField(idField);
	}

	@Override
	public T add(T po) {
		SqlAndParams sqlAndParams = this.sqlDialect.add(po);
		jdbcTemplate.update(sqlAndParams.getSql());
		Serializable idValue = getIdValue(po);
		if (null != idValue) {
			return findOneById(idValue);
		}
		return null;
	}

	@Override
	public int delete(T po, String... field) {
		return 0;
	}

	@Override
	public int delete() {
		return 0;
	}

	@Override
	public Long count() {
		SqlAndParams sqlAndParams = this.sqlDialect.count(entryClass, null);
		return jdbcTemplate.queryForLong(sqlAndParams.getSql(), sqlAndParams.getParamArray());
	}

	@Override
	public Long count(T po, String... field) {
		Set<String> filter = Sets.newHashSet(field);
		Cache<Field, String> fieldCache = DbCache.getFieldCache(entryClass);
		SqlAndParams sqlAndParams = this.sqlDialect.count(entryClass, new LinkedHashMap<String, Object>(){
			{
				for (Field item : fieldCache.keySet()) {
					if (!filter.isEmpty() && !filter.contains(item.getName())) {
						continue;
					}
					this.put(fieldCache.get(item), ReflectUtil.getFieldValue(item, po));
				}
			}
		});
		return jdbcTemplate.queryForLong(sqlAndParams.getSql(), sqlAndParams.getParamArray());
	}

	@Override
	public T findOneById(Serializable id) {
		SqlAndParams sqlAndParams = this.sqlDialect.find(entryClass, new LinkedHashMap<String, Object>(){
			{
				this.put(idColumnName, id);
			}
		});
		return jdbcTemplate.query(entryClass, sqlAndParams);
	}

	@Override
	public T findOne(T po, String... field) {
		Set<String> filter = Sets.newHashSet(field);
		Cache<Field, String> fieldCache = DbCache.getFieldCache(entryClass);
		SqlAndParams sqlAndParams = this.sqlDialect.find(entryClass, new LinkedHashMap<String, Object>(){
			{
				for (Field item : fieldCache.keySet()) {
					if (!filter.isEmpty() && !filter.contains(item.getName())) {
						continue;
					}
					this.put(fieldCache.get(item), ReflectUtil.getFieldValue(item, po));
				}
			}
		});
		return jdbcTemplate.query(entryClass, sqlAndParams);
	}

	@Override
	public List<T> find() {
		SqlAndParams sqlAndParams = this.sqlDialect.find(entryClass, null);
		return jdbcTemplate.queryForList(entryClass, sqlAndParams);
	}

	@Override
	public List<T> find(T po, String... field) {
		Set<String> filter = Sets.newHashSet(field);
		Cache<Field, String> fieldCache = DbCache.getFieldCache(entryClass);
		SqlAndParams sqlAndParams = this.sqlDialect.find(entryClass, new LinkedHashMap<String, Object>(){
			{
				for (Field item : fieldCache.keySet()) {
					if (!filter.isEmpty() && !filter.contains(item.getName())) {
						continue;
					}
					this.put(fieldCache.get(item), ReflectUtil.getFieldValue(item, po));
				}
			}
		});
		return jdbcTemplate.queryForList(entryClass, sqlAndParams);
	}

	@Override
	public List<T> findPage(int beginNo, int pageSize) {
		return null;
	}

	@Override
	public int updateById(T po) {
		return 0;
	}

	@Override
	public int updateById(T po, String... field) {
		return 0;
	}

	@Override
	public int deleteById(Serializable id) {
		return 0;
	}

	@Override
	public List<T> findPage(T po, int beginNo, int pageSize, String... field) {
		return null;
	}

	private <V> V getIdValue(Object obj) {
		if (null == obj) {
			return null;
		}
		return (V)ReflectUtil.getFieldValue(idField, obj);
	}

	private static Field getIdField(Class<?> clazz) {
		List<Field> fieldList = DbCache.getFieldList(clazz);
		if (CollectionUtil.isEmpty(fieldList)) {
			return null;
		}
		Field res  = null;
		for (Field field : fieldList) {
			if (field.isAnnotationPresent(Id.class)) {
				return field;
			}
			if (field.getName().equals("id")) {
				res = field;
			}
		}
		return res;
	}
}
