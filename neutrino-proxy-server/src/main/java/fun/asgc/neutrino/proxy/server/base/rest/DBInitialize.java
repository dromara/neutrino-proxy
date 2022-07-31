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
package fun.asgc.neutrino.proxy.server.base.rest;

import fun.asgc.neutrino.core.annotation.PreLoad;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.core.util.LockUtil;
import fun.asgc.neutrino.core.util.StringUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author: 初始化数据库
 * @date: 2022/7/31
 */
@PreLoad("init")
public class DBInitialize {

	private static Connection conn;

	public static void init() throws Exception {
		initDBStructure();
	}

	/**
	 * 初始化数据库结构
	 */
	private static void initDBStructure() throws Exception {
		Statement stat = getOrNewConnection().createStatement();
		List<String> lines = FileUtil.readContentAsStringList("classpath:/init-structure.sql");
		if (CollectionUtil.isEmpty(lines)) {
			return;
		}
		String sql = "";
		for (String line : lines) {
			if (StringUtil.isEmpty(line) || StringUtil.isEmpty(line.trim()) || line.trim().startsWith("#")) {
				continue;
			}
			sql += "\r\n" + line.trim();
			if (sql.endsWith(";")) {
				stat.executeUpdate(sql);
				sql = "";
			}
		}
	}

	/**
	 * 获取一个数据库连接，如果数据库不存在，则会创建一个空数据库
	 * @return
	 * @throws Exception
	 */
	public static Connection getOrNewConnection() throws Exception {
		return LockUtil.doubleCheckProcess(
			() -> null == conn,
			DBInitialize.class,
			() -> {
				Class.forName("org.sqlite.JDBC");
				//建立一个数据库名data.db的连接，如果不存在就在当前目录下创建之
				DBInitialize.conn = DriverManager.getConnection("jdbc:sqlite:data.db");
			},
			() -> conn
		);
	}
}
