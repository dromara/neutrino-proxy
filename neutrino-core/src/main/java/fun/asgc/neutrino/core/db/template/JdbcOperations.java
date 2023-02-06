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

import fun.asgc.neutrino.core.db.annotation.Id;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.ReflectUtil;
import fun.asgc.neutrino.core.util.TypeUtil;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public class JdbcOperations {
	private static final JdbcOperations instance = new JdbcOperations();
	
	private static final Map<Class<?>, Field> generateIdFieldMap = new ConcurrentHashMap<>();

	private JdbcOperations() {

	}

	public static JdbcOperations getInstance() {
		return instance;
	}

	/**
	 * 通用执行方法
	 * @param callback
	 * @param <T>
	 * @return
	 */
	public <T> T execute(JdbcCallback<T> callback) throws SQLException {
		return callback.execute();
	}

	/**
	 * 执行更新操作
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 */
	public int executeUpdate(final Connection conn , final String sql, final Object[] params) throws SQLException {
		return this.execute(new PreparedStatementJdbcCallback<Integer>(){
			@Override
			public Integer execute(PreparedStatement ps) throws SQLException {
				return ps.executeUpdate();
			}
			@Override
			public Object[] getParams() {
				return params;
			}
			@Override
			public String getSql() {
				return sql;
			}
			@Override
			public Connection getConnection(){
				return conn;
			}
		});
	}

	/**
	 * 执行更新操作
	 * 临时兼容返设主键问题
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 */
	public int executeUpdateByModel(final Connection conn , final String sql, final Object model, final Object[] params) throws SQLException {
		return this.execute(new PreparedStatementJdbcCallback<Integer>(){
			@Override
			public Integer execute(PreparedStatement ps) throws SQLException {
				Integer res = ps.executeUpdate();
				if (null != model) {
					ResultSet resultSet = ps.getGeneratedKeys();
					if (resultSet.next()) {
						Field field = getGenerateIdField(model.getClass());
						if (null != field) {
							ReflectUtil.setFieldValue(field, model, resultSet.getInt(1));
						}
					}
				}
				return res;
			}
			@Override
			public Object[] getParams() {
				return params;
			}
			@Override
			public String getSql() {
				return sql;
			}
			@Override
			public Connection getConnection(){
				return conn;
			}
		});
	}

	/**
	 * 执行单条查询操作
	 * @param conn
	 * @param clazz
	 * @param sql
	 * @param params
	 * @param <T>
	 * @return
	 */
	public <T> T executeQuery(final Connection conn,final Class<T> clazz,final String sql,final Object[] params) throws SQLException {
		return this.execute(new PreparedStatementJdbcCallback<T>() {

			@Override
			public T execute(PreparedStatement ps) {
				T obj = null;

				try{
					ResultSet resultSet = ps.executeQuery();
					if(resultSet.next()){
						if(TypeUtil.isNormalBasicType(clazz)){
							Object value = resultSet.getObject(1);
							obj = TypeUtil.conversion(value, clazz);
						}else if(TypeUtil.isMap(clazz)){
							Map<String,Object> map = new HashMap<String,Object>();
							obj = (T)map;

							ResultSetMetaData rsmd = resultSet.getMetaData();
							int columnCount = rsmd.getColumnCount();
							for(int i = 1;i <= columnCount;i++){
								String name = rsmd.getColumnName(i);
								Object value = resultSet.getObject(i);
								map.put(DbCache.fromColumnName(name), value);
							}
						}else{
							obj = clazz.newInstance();
							ResultSetMetaData rsmd = resultSet.getMetaData();
							int columnCount = rsmd.getColumnCount();
							for(int i = 1;i <= columnCount;i++){
								String name = rsmd.getColumnName(i);
								Object value = resultSet.getObject(i);
								List<Field> fieldList = DbCache.getField(clazz, name);
								ReflectUtil.setFieldValue(fieldList, obj, value);
							}
						}
					}
				}catch(Exception e){
					throw new RuntimeException(e);
				}
				return obj;
			}
			@Override
			public Connection getConnection() {
				return conn;
			}
			@Override
			public Object[] getParams() {
				return params;
			}
			@Override
			public String getSql() {
				return sql;
			}
		});
	}

	/**
	 * 执行多条查询操作
	 * @param conn
	 * @param clazz
	 * @param sql
	 * @param params
	 * @param <T>
	 * @return
	 */
	public <T> List<T> executeQueryForList(final Connection conn, final Class<T> clazz, final String sql, final Object[] params) throws SQLException {
		return this.execute(new PreparedStatementJdbcCallback<List<T>>() {
			@Override
			public List<T> execute(PreparedStatement ps) {
				List<T> res = new ArrayList<T>();
				try{
					ResultSet resultSet = ps.executeQuery();
					ResultSetMetaData rsmd = resultSet.getMetaData();
					int columnCount = rsmd.getColumnCount();

					while(resultSet.next()){
						T obj = null;

						if(TypeUtil.isNormalBasicType(clazz)){
							Object value = resultSet.getObject(1);
							obj = TypeUtil.conversion(value, clazz);
						}else if(TypeUtil.isMap(clazz)){
							Map<String,Object> map = new HashMap<String,Object>();
							obj = (T)map;

							for(int i = 1;i <= columnCount;i++){
								String name = rsmd.getColumnName(i);
								Object value = resultSet.getObject(i);
								map.put(DbCache.fromColumnName(name), value);
							}
						}else{
							obj = clazz.newInstance();

							for(int i = 1;i <= columnCount;i++){
								String name = rsmd.getColumnName(i);
								Object value = resultSet.getObject(i);
								List<Field> fieldList = DbCache.getField(clazz, name);
								ReflectUtil.setFieldValue(fieldList, obj, value);
							}
						}

						res.add(obj);
					}

				}catch(Exception e){
					throw new RuntimeException(e);
				}

				return res;
			}

			@Override
			public Connection getConnection() {

				return conn;
			}

			@Override
			public Object[] getParams() {

				return params;
			}

			@Override
			public String getSql() {

				return sql;
			}

		});
	}

	/**
	 * 获取自动生成ID字段
	 * @param clazz
	 * @return
	 */
	private static Field getGenerateIdField(Class<?> clazz) {
		if (null == clazz) {
			return null;
		}
		if (generateIdFieldMap.containsKey(clazz)) {
			return generateIdFieldMap.get(clazz);
		}
		Set<Field> fields = ReflectUtil.getDeclaredFields(clazz);
		if (CollectionUtil.isEmpty(fields)) {
			return null;
		}
		Field field = fields.stream().filter(f -> f.isAnnotationPresent(Id.class)).findFirst().orElse(null);
		if (null != field) {
			return field;
		}
		field = fields.stream().filter(f -> f.getName().equals("id")).findFirst().orElse(null);
		return field;
	}
}
