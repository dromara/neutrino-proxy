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
package fun.asgc.neutrino.core.db.template;

import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.cache.Cache;
import fun.asgc.neutrino.core.cache.MemoryCache;
import fun.asgc.neutrino.core.cache.MemoryCacheGroup;
import fun.asgc.neutrino.core.db.annotation.Column;
import fun.asgc.neutrino.core.db.annotation.NotColumn;
import fun.asgc.neutrino.core.db.annotation.Table;
import fun.asgc.neutrino.core.util.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * 数据库相关缓存
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public class DbCache {
	/**
	 * 名称映射缓存组
	 */
	private static final MemoryCacheGroup<String, String> nameMappingCache = new MemoryCacheGroup<>();
	private static final String GROUP_COLUMN_NAME_TO = "GROUP_COLUMN_NAME_TO";
	private static final String GROUP_COLUMN_NAME_FROM = "GROUP_COLUMN_NAME_FROM";
	private static final String GROUP_TABLE_NAME_TO = "GROUP_TABLE_NAME_TO";
	private static final String GROUP_TABLE_NAME_FROM = "GROUP_TABLE_NAME_FROM";
	private static final Cache<Class<?>, Cache<Field, String>> fieldToColumnCache = new MemoryCache<>();
	private static final Object fieldToColumnCacheLock = new Object();
	private static final Cache<Class<?>,String> classTableNameCache = new MemoryCache<>();

	/**
	 * 转换为列名
	 * @param s
	 * @return
	 */
	public static String toColumnName(String s) {
		return LockUtil.doubleCheckProcess(
			() -> !nameMappingCache.containsKey(GROUP_COLUMN_NAME_TO, s),
			GROUP_COLUMN_NAME_TO,
			() -> nameMappingCache.set(GROUP_COLUMN_NAME_TO, s, DefaultColumnNameConvert.getInstance().to(s)),
			() -> nameMappingCache.get(GROUP_COLUMN_NAME_TO, s)
		);
	}

	/**
	 * 转换为字段名
	 * @param s
	 * @return
	 */
	public static String fromColumnName(String s) {
		return LockUtil.doubleCheckProcess(
			() -> !nameMappingCache.containsKey(GROUP_COLUMN_NAME_FROM, s),
			GROUP_COLUMN_NAME_FROM,
			() -> nameMappingCache.set(GROUP_COLUMN_NAME_FROM, s, DefaultColumnNameConvert.getInstance().from(s)),
			() -> nameMappingCache.get(GROUP_COLUMN_NAME_FROM, s)
		);
	}

	/**
	 * 转换为表名
	 * @param s
	 * @return
	 */
	public static String toTableName(String s) {
		return LockUtil.doubleCheckProcess(
			() -> !nameMappingCache.containsKey(GROUP_TABLE_NAME_TO, s),
			GROUP_TABLE_NAME_TO,
			() -> nameMappingCache.set(GROUP_TABLE_NAME_TO, s, DefaultTableNameConvert.getInstance().to(s)),
			() -> nameMappingCache.get(GROUP_TABLE_NAME_TO, s)
		);
	}

	/**
	 * 转换为表名
	 * @param clazz
	 * @return
	 */
	public static String toTableName(Class<?> clazz) {
		return LockUtil.doubleCheckProcess(() -> !classTableNameCache.containsKey(clazz),
			clazz,
			() -> {
				Table table = clazz.getAnnotation(Table.class);
				String tableName;
				if (null != table && StringUtil.notEmpty(table.value())) {
					tableName = table.value();
				} else {
					tableName =  toTableName(TypeUtil.getSimpleName(clazz));
				}
				classTableNameCache.set(clazz, tableName);
			},
			() -> classTableNameCache.get(clazz)
		);
	}

	/**
	 * 转换为实体名
	 * @param s
	 * @return
	 */
	public static String fromTableName(String s) {
		return LockUtil.doubleCheckProcess(
			() -> !nameMappingCache.containsKey(GROUP_TABLE_NAME_FROM, s),
			GROUP_TABLE_NAME_FROM,
			() -> nameMappingCache.set(GROUP_TABLE_NAME_FROM, s, DefaultTableNameConvert.getInstance().from(s)),
			() -> nameMappingCache.get(GROUP_TABLE_NAME_FROM, s)
		);
	}

	/**
	 * 根据类+列名获取字段
	 * @param clazz
	 * @param column
	 * @return
	 */
	public static List<Field> getField(Class<?> clazz, String column) {
		return LockUtil.doubleCheckProcess(
			() -> !fieldToColumnCache.containsKey(clazz),
			fieldToColumnCacheLock,
			() -> initFieldCache(clazz),
			() -> {
				List<Field> list = Lists.newArrayList();
				Cache<Field, String> cache = fieldToColumnCache.get(clazz);
				if (null == cache || cache.isEmpty()) {
					return list;
				}
				for (Field field : cache.keySet()) {
					if (cache.get(field).equals(column)) {
						list.add(field);
					}
				}

				return list;
			}
		);
	}

	/**
	 * 根据类名获取列名列表
	 * @param clazz
	 * @return
	 */
	public static List<Field> getFieldList(Class<?> clazz) {
		return LockUtil.doubleCheckProcess(
			() -> !fieldToColumnCache.containsKey(clazz),
			fieldToColumnCacheLock,
			() -> initFieldCache(clazz),
			() -> {
				List<Field> list = Lists.newArrayList();
				Cache<Field, String> cache = fieldToColumnCache.get(clazz);
				if (null == cache || cache.isEmpty()) {
					return list;
				}
				list.addAll(cache.keySet());
				return list;
			}
		);
	}

	/**
	 * 根据类获取字段缓存
	 * @param clazz
	 * @return
	 */
	public static Cache<Field, String> getFieldCache(Class<?> clazz) {
		return LockUtil.doubleCheckProcess(
			() -> !fieldToColumnCache.containsKey(clazz),
			clazz,
			() -> initFieldCache(clazz),
			() -> fieldToColumnCache.get(clazz)
		);
	}

	public static String getColumnNameByField(Field field) {
		return LockUtil.doubleCheckProcess(
			() -> !fieldToColumnCache.containsKey(field.getDeclaringClass()),
			fieldToColumnCacheLock,
			() -> initFieldCache(field.getDeclaringClass()),
			() -> {
				if (!fieldToColumnCache.containsKey(field.getDeclaringClass()) || !fieldToColumnCache.get(field.getDeclaringClass()).containsKey(field)) {
					return null;
				}
				return fieldToColumnCache.get(field.getDeclaringClass()).get(field);
			}
		);
	}

	/**
	 * 初始化字段缓存
	 * @param clazz
	 */
	private static void initFieldCache(Class<?> clazz) {
		Set<Field> fieldSet = ReflectUtil.getDeclaredFields(clazz);
		if (CollectionUtil.isEmpty(fieldSet)) {
			return;
		}
		Cache<Field, String> cache = new MemoryCache<>();
 		fieldSet.forEach(field -> {
			if (field.isAnnotationPresent(NotColumn.class)) {
				return;
			}
			Column column = field.getAnnotation(Column.class);
			if (null != column && StringUtil.notEmpty(column.value())) {
				cache.set(field, column.value());
			} else {
				cache.set(field, toColumnName(field.getName()));
			}

		});

 		fieldToColumnCache.set(clazz, cache);
	}
}
