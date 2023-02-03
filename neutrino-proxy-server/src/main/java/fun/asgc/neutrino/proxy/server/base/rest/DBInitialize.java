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

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import fun.asgc.neutrino.core.annotation.PreLoad;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.core.util.*;
import fun.asgc.neutrino.proxy.server.base.rest.config.DbConfig;
import fun.asgc.neutrino.proxy.server.constant.DbTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.DriverManager;
import java.util.List;

/**
 * 初始化数据库
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Slf4j
@PreLoad("init")
public class DBInitialize {
	private static List<String> initDataTableNameList = Lists.newArrayList("user", "license", "port_pool", "port_mapping", "job_info");
	private static DbConfig dbConfig;
	private static JdbcTemplate jdbcTemplate;

	private static DbTypeEnum dbTypeEnum;

	public static void init() throws Exception {
		dbConfig = ConfigUtil.getYmlConfig(DbConfig.class);
		Assert.notNull(dbConfig.getType(), "neutrino.data.db.type不能为空!");
		dbTypeEnum = DbTypeEnum.of(dbConfig.getType());
		Assert.notNull(dbTypeEnum, "neutrino.data.db.type取值异常!");

		log.info("{}数据库初始化...", dbConfig.getType());
		jdbcTemplate = getJdbcTemplate();
		initDBStructure();
		initDBData();
	}

	/**
	 * 初始化数据库结构
	 */
	private static void initDBStructure() throws Exception {
		List<String> lines = FileUtil.readContentAsStringList(String.format("classpath:/sql/%s/init-structure.sql", dbConfig.getType()));
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
				log.debug("初始化数据库表 sql:{}", sql);
				jdbcTemplate.update(sql);
				sql = "";
			}
		}
	}

	/**
	 * 初始化数据
	 * @throws Exception
	 */
	private static void initDBData() throws Exception {
		if (CollectionUtil.isEmpty(initDataTableNameList)) {
			return;
		}
		for (String tableName : initDataTableNameList) {
			// 表里没有数据的时候，才进行初始化操作
			int count = jdbcTemplate.queryForInt(String.format("select count(1) from `%s`", tableName));
			if (count > 0) {
				continue;
			}
			List<String> lines = FileUtil.readContentAsStringList(String.format("classpath:/sql/%s/%s.data.sql", dbConfig.getType(), tableName));
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
					log.debug("初始化数据[table={}] sql:{}", tableName, sql);
					jdbcTemplate.update(sql);
					sql = "";
				}
			}
		}
	}

	/**
	 * 获取jdbcTemplate实例
	 * @return
	 * @throws Exception
	 */
	private static JdbcTemplate getJdbcTemplate() throws Exception {
		return LockUtil.doubleCheckProcess(
			() -> null == jdbcTemplate,
			DBInitialize.class,
			() -> {
				if (DbTypeEnum.SQLITE == dbTypeEnum) {
					//建立一个数据库名data.db的连接，如果不存在就在当前目录下创建之
					DriverManager.getConnection(dbConfig.getUrl());
					// 创建数据源
					SQLiteDataSource dataSource = new SQLiteDataSource();
					dataSource.setUrl(dbConfig.getUrl());
					dataSource.setJournalMode(SQLiteConfig.JournalMode.WAL.getValue());
					// 创建jdbcTemplate
					jdbcTemplate = new JdbcTemplate(dataSource);
				} else if (DbTypeEnum.MYSQL == dbTypeEnum) {
					DruidDataSource dataSource = new DruidDataSource();
					dataSource.setDriverClassName(dbConfig.getDriverClass());
					dataSource.setUrl(dbConfig.getUrl());
					dataSource.setInitialSize(5);
					dataSource.setMinIdle(5);
					dataSource.setMaxActive(20);
					dataSource.setMaxWait(60000);
					dataSource.setPoolPreparedStatements(true);
					dataSource.setUsername(dbConfig.getUsername());
					dataSource.setPassword(dbConfig.getPassword());

					// 创建jdbcTemplate
					jdbcTemplate = new JdbcTemplate(dataSource);
				}
			},
			() -> jdbcTemplate
		);
	}
}
