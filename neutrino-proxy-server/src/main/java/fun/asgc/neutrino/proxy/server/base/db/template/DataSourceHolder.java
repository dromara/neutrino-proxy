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
package fun.asgc.neutrino.proxy.server.base.db.template;

import fun.asgc.neutrino.core.db.dao.DBType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源持有者
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class DataSourceHolder {
	/**
	 * 数据源
	 */
	private DataSource dataSource;
	/**
	 * 是否保持连接
	 * 默认执行完SQL操作就归还连接
	 * 在开启事务的情况下，事务操作完毕才能归还
	 */
	private ThreadLocal<Boolean> keepConnectionCache = new ThreadLocal<>();
	/**
	 * 数据库类型
	 */
	private DBType dbType;

	public DataSourceHolder(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSourceHolder(DataSource dataSource, DBType dbType) {
		this.dataSource = dataSource;
		this.dbType = dbType;
	}

	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	public void close(Connection conn) throws SQLException {
		conn.close();
	}

	public void tryClose(Connection conn) throws SQLException {
		Boolean keepConnection = keepConnectionCache.get();
		if (null == keepConnection || !keepConnection) {
			conn.close();
		}
	}

	public void setKeepConnection(Boolean keepConnection) {
		this.keepConnectionCache.set(keepConnection);
	}

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(DBType dbType) {
		this.dbType = dbType;
	}
}
