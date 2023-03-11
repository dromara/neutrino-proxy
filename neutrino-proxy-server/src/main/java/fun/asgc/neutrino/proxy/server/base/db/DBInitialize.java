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
package fun.asgc.neutrino.proxy.server.base.db;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Db;
import fun.asgc.neutrino.proxy.core.util.Assert;
import fun.asgc.neutrino.proxy.core.util.FileUtil;
import fun.asgc.neutrino.proxy.server.constant.DbTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import java.util.List;

/**
 * 初始化数据库
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Slf4j
@Component
public class DBInitialize implements EventListener<AppLoadEndEvent> {
	private static List<String> initDataTableNameList = Lists.newArrayList("user", "license", "port_pool", "port_mapping", "job_info");
	@Inject
	private DbConfig dbConfig;
	private DbTypeEnum dbTypeEnum;
//	@Inject
//	private JdbcTemplate jdbcTemplate;
//	@Inject
//	private DataSource dataSource;


	@Init
	public void init() throws Throwable {
		Assert.notNull(dbConfig.getType(), "neutrino.data.db.type不能为空!");
		dbTypeEnum = DbTypeEnum.of(dbConfig.getType());
		Assert.notNull(dbTypeEnum, "neutrino.data.db.type取值异常!");

		log.info("{}数据库初始化...", dbConfig.getType());
		initDBStructure();
		initDBData();
	}

	@Override
	public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
		// TODO 该事件有50%的概率不触发
	}

	/**
	 * 初始化数据库结构
	 */
	private void initDBStructure() throws Exception {
		List<String> lines = FileUtil.readContentAsStringList(String.format("classpath:/sql/%s/init-structure.sql", dbConfig.getType()));
		if (CollectionUtil.isEmpty(lines)) {
			return;
		}
		String sql = "";
		for (String line : lines) {
			if (StrUtil.isEmpty(line) || StrUtil.isEmpty(line.trim()) || line.trim().startsWith("#")) {
				continue;
			}
			sql += "\r\n" + line.trim();
			if (sql.endsWith(";")) {
				log.debug("初始化数据库表 sql:{}", sql);
				Db.update(sql);
				sql = "";
			}
		}
	}

	/**
	 * 初始化数据
	 * @throws Exception
	 */
	private void initDBData() throws Exception {
		if (CollectionUtil.isEmpty(initDataTableNameList)) {
			return;
		}
		for (String tableName : initDataTableNameList) {
			// 表里没有数据的时候，才进行初始化操作

			int count = Db.queryInt(String.format("select count(1) from `%s`", tableName));
			if (count > 0) {
				continue;
			}
			List<String> lines = FileUtil.readContentAsStringList(String.format("classpath:/sql/%s/%s.data.sql", dbConfig.getType(), tableName));
			if (CollectionUtil.isEmpty(lines)) {
				return;
			}
			String sql = "";
			for (String line : lines) {
				if (StrUtil.isEmpty(line) || StrUtil.isEmpty(line.trim()) || line.trim().startsWith("#")) {
					continue;
				}
				sql += "\r\n" + line.trim();
				if (sql.endsWith(";")) {
					log.debug("初始化数据[table={}] sql:{}", tableName, sql);
					Db.update(sql);
					sql = "";
				}
			}
		}
	}
}
