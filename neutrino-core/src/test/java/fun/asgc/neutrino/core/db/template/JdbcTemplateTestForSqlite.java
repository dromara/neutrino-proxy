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

import com.zaxxer.hikari.HikariDataSource;
import fun.asgc.neutrino.core.db.annotation.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/8
 */
public class JdbcTemplateTestForSqlite {
	private JdbcTemplate jdbcTemplate;

	{
		HikariDataSource dataSource = new HikariDataSource();
//		dataSource.setUrl("jdbc:sqlite:" + JdbcTemplateTestForSqlite.class.getResource("/sqlite.db").getPath());
		dataSource.setJdbcUrl("jdbc:sqlite:../data.db");
		dataSource.setDriverClassName("org.sqlite.JDBC");
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void 新增数据1() throws SQLException {
		jdbcTemplate.update("insert into user(`id`,`name`,`age`,`email`,`sex`,`create_time`) values(?,?,?,?,?,?)", 1, "张三", 21, "zhangsan@qq.com", "男", new Date());
	}

	@Test
	public void 查询单个行记录2() throws SQLException {
		User user = jdbcTemplate.query(User.class, "select * from user where id = 1");
		System.out.println(user);
	}

	@Test
	public void 查询列表() throws SQLException {
		// 此处只能传数组，不能传集合
		List<User> userList = jdbcTemplate.queryForList(User.class, "select * from user where id in (?,?,?,?)", new Object[]{1,2,3,4});
		System.out.println(userList);
	}

	@Accessors(chain = true)
	@Data
	public static class User {
		@Id
		private Long id;
		private String name;
		private Integer age;
		private String email;
		private String sex;
		private Date createTime;
		private Date updateTime;
	}

}

