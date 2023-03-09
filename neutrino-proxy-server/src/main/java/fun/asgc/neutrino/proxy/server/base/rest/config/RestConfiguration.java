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
package fun.asgc.neutrino.proxy.server.base.rest.config;

import com.alibaba.druid.pool.DruidDataSource;
import fun.asgc.neutrino.core.db.template.JdbcTemplate;
import fun.asgc.neutrino.proxy.server.constant.DbTypeEnum;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * 系统配置
 *author: aoshiguchen
 * @date: 2022/8/1
 */
@Configuration
public class RestConfiguration {
	@Inject
	private DbConfig dbConfig;

	@Bean("dataSource")
	public DataSource dataSource() {
		DbTypeEnum dbTypeEnum = DbTypeEnum.of(dbConfig.getType());
		if (DbTypeEnum.SQLITE == dbTypeEnum) {
			SQLiteDataSource dataSource = new SQLiteDataSource();
			dataSource.setUrl(dbConfig.getUrl());
			dataSource.setJournalMode(SQLiteConfig.JournalMode.WAL.getValue());
			return dataSource;
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
			return dataSource;
		}

		return null;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(@Inject("dataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
