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

import com.alibaba.druid.pool.DruidDataSource;
import fun.asgc.neutrino.core.db.annotation.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/27
 */
public class JdbcTemplateTest {
	private JdbcTemplate jdbcTemplate;

	{
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/test1?useUnicode=true&characterEncoding=utf8");
		dataSource.setUsername("root");
		dataSource.setPassword("YWasgc@10520");
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void 新增数据1() {
		jdbcTemplate.update("insert into user(`id`,`name`,`age`,`email`,`sex`,`create_time`) values(?,?,?,?,?,?)", 1, "张三", 21, "zhangsan@qq.com", "男", new Date());
	}

	@Test
	public void 数据新增2() {
		Map<String, Object> params = new HashMap<>();
		params.put("id", 2);
		params.put("name", "李四");
		params.put("age", 22);
		params.put("email", "lisi@qq.com");
		params.put("sex", "女");
		params.put("createTime", new Date());
		jdbcTemplate.updateByMap("insert into user(`id`,`name`,`age`,`email`,`sex`,`create_time`) values(:id,:name,:age,:email,:sex,:createTime)", params);
	}

	@Test
	public void 数据新增3() {
		User user = new User();
		user.setId(3L);
		user.setName("王五");
		user.setAge(23);
		user.setEmail("wangwu@qq.com");
		user.setSex("男");
		user.setCreateTime(new Date());
		jdbcTemplate.updateByModel("insert into user(`id`,`name`,`age`,`email`,`sex`,`create_time`) values(:id,:name,:age,:email,:sex,:createTime)", user);
	}

	@Test
	public void 更新数据1() {
		jdbcTemplate.update("update user set age = ? ,update_time = ? where id = ?", 31, new Date(), 1);
	}

	@Test
	public void 更新数据2() {
		Map<String, Object> params = new HashMap<>();
		params.put("id", 2);
		params.put("updateTime", new Date());
		params.put("age", 32);
		jdbcTemplate.updateByMap("update user set age = :age ,update_time = :updateTime where id = :id", params);
	}

	@Test
	public void 更新数据3() {
		User user = new User();
		user.setId(3L);
		user.setAge(33);
		user.setUpdateTime(new Date());
		jdbcTemplate.updateByModel("update user set age = :age ,update_time = :updateTime where id = :id", user);
	}

	@Test
	public void 删除数据1() {
		jdbcTemplate.update("delete from user where id = ?",  1L);
	}

	@Test
	public void 删除数据2() {
		Map<String, Object> params = new HashMap<>();
		params.put("id", 2);
		jdbcTemplate.updateByMap("delete from user where id = :id", params);
	}

	@Test
	public void 删除数据3() {
		User user = new User();
		user.setId(3L);
		jdbcTemplate.updateByModel("delete from user where id = :id", user);
	}

	@Test
	public void 查询单个行记录1() {
		Map<String, Object> map = jdbcTemplate.queryForMap("select * from user where id = ?", 1);
		System.out.println(map);
	}

	@Test
	public void 查询单个行记录2() {
		User user = jdbcTemplate.query(User.class, "select * from user where id = 1");
		System.out.println(user);
	}

	@Test
	public void 查询单个字段记录1() {
		String name = jdbcTemplate.queryForString("select name from user where id = ?", 1);
		System.out.println(name);
	}

	@Test
	public void 查询单个字段记录2() {
		int age = jdbcTemplate.queryForInt("select age from user where id = 1");
		System.out.println(age);
	}

	@Test
	public void 查询单个字段记录3() {
		Long count = jdbcTemplate.queryForLong("select count(1) from user");
		System.out.println(count);
	}

	@Test
	public void 查询多个行记录1() {
		List<Map> list = jdbcTemplate.queryForListMap("select * from user");
		System.out.println(list);
	}

	@Test
	public void 查询多个行记录2() {
		List<User> list = jdbcTemplate.queryForList(User.class, "select * from user");
		System.out.println(list);
	}

	@Test
	public void 查询多个字段记录1() {
		List<Integer> list = jdbcTemplate.queryForListInt("select age from user");
		System.out.println(list);
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
