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

import fun.asgc.neutrino.core.db.annotation.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
public class SqlDialectForMysqlTest {

	@Test
	public void test1() {
		SqlDialect sqlDialect = new SqlDialectForMysql();
		User user = new User();
		user.setName("11'");
		user.setAge(20);
		user.setSex("男");
		user.setEmail("11@qq.com");
		user.setCreateTime(new Date());
		System.out.println(sqlDialect.add(user));
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
