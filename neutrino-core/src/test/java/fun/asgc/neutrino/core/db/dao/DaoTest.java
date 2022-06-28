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

import com.alibaba.druid.pool.DruidDataSource;
import fun.asgc.neutrino.core.db.annotation.Column;
import fun.asgc.neutrino.core.db.annotation.Id;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class DaoTest {
	private DruidDataSource dataSource;
	private DBType dbType;

	{
		dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/test1?useUnicode=true&characterEncoding=utf8");
		dataSource.setUsername("root");
		dataSource.setPassword("YWasgc@10520");
		dbType = DBType.MYSQL;
	}

	@Test
	public void add() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		User user = new User();
		user.setId(4L);
		user.setName("赵六");
		user.setAge(24);
		user.setEmail("zhaoliu@qq.com");
		user.setSex("女");
		user.setCreateTime22(new Date());
		userDao.add(user);
	}

	@Test
	public void findOneById() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		User user = userDao.findOneById(3);
		System.out.println(user);
	}

	@Test
	public void findOne1() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		User user = userDao.findOne(new User()
		.setId(1L), "id");
		System.out.println(user);
	}

	@Test
	public void findOne2() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		User user = userDao.findOne(new User()
			.setAge(23), "age");
		System.out.println(user);
	}

	@Test
	public void findOne3() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		User user = userDao.findOne(new User()
			.setAge(23)
			.setName("张三"), "age", "name");
		System.out.println(user);
	}

	@Test
	public void find1() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		List<User> userList = userDao.find();
		System.out.println(userList);
	}

	@Test
	public void find2() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		List<User> userList = userDao.find(new User().setAge(21), "age");
		System.out.println(userList);
	}

	@Test
	public void count1() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		System.out.println(userDao.count());
	}

	@Test
	public void count2() {
		Dao<User> userDao = new DefaultDaoImpl<>(dataSource, dbType, User.class);
		System.out.println(userDao.count(new User().setAge(21), "age"));
	}

	@ToString
	@Accessors(chain = true)
	@Data
	public static class User {
		@Id
		private Long id;
		private String name;
		private Integer age;
		private String email;
		private String sex;
		@Column("create_time")
		private Date createTime22;
		private Date updateTime;
	}
}
